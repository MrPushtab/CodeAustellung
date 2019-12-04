///////////////////////////////////////////////////////////////////////////////
// matmul.c
// Author: Dr. Richard A. Goodrum, Ph.D.
// Tobias Kroll
// Procedures:
//
// main			tests matmul
// matmul		basic, brute force matrix multiply
// genMatrices 	fills the matrices in the local process
///////////////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <sys/time.h>
#include <mpi.h>
#include <math.h>

///////////////////////////////////////////////////////////////////////////////
// int main( int argc, char *argv[] )
// Author: Dr. Richard A. Goodrum, Ph.D.
// Date:  16September 2017
// Description: Generates two matrices and then calls matmul to multiply them.
// 	Finally, it verifies that the results are correct.
//
// Parameters:
//	argc	I/P	int	The number of arguments on the command line
//	argv	I/P	char *[]	The arguments on the command line
//	main	O/P	int	Status code
///////////////////////////////////////////////////////////////////////////////
// altered by Tobias Kroll on 20 October 2018 to implement Cannon's algorithm
/////////////////////////////////////////////////////////////////////////////////
#define L (16384)
#define M (16384)
#define N (16384)
//#define L (128)
//#define M (128)
//#define N (128)
//#define L (8192)
//#define M (8192)
//#define N (8192)
//float A[L*M], B[M*N], C[L*N];

int main( int argc, char *argv[] )
{
	//steps:
	//1. set up MPI as well as CartComm
	//2. traverse cart comm to find the processes adjacent to the current one
	//		-left, right, up, down
	//2.5. set up the local matrices (later)
	//3. loop:
	//		a. mulmatrix
	//		b. shift the matrices
	
	//This is step 1: setting up the MPI processes and lattice
	int i, j, k;
	int bigI, bigJ; //process locations in the lattice
	int numprocesses; //number of processes
	MPI_Init(&argc, &argv); 
	MPI_Comm_size(MPI_COMM_WORLD, &numprocesses);
	
	//sid=this processes id
	int sid;
	
	//setting up the cartesian lattice
	int dims[2],period[2];
	period[0]=1;period[1]=1;
	dims[0]=0;dims[1]=0;
	//Side Note: Dims create was occasionally giving me 2x8 matrices for certain runs?
	MPI_Dims_create(numprocesses, 2, dims);
	MPI_Comm cartComm;
	MPI_Cart_create(MPI_COMM_WORLD,2,dims,period,1,&cartComm);
	MPI_Status status; 
	MPI_Comm_rank(cartComm, &sid);
	//setting up coordinate variables for getting neighboring processes
	int scoords[2];
	MPI_Cart_coords(cartComm,sid,2,scoords);
	bigI = dims[0]; bigJ = dims[1];
	int lid,rid,uid,did,coords[2];
	
	///the following calculates the neighboring processes in the cartesian communicator.
	//////////////// left
	//coords[0]=scoords[0]-1;
	//coords[1]=scoords[1];
	//if(coords[0]<0)
	//{
	//	coords[0]+=dims[0];
	//}
	//MPI_Cart_rank(cartComm, coords, &lid);
	////////////////////////////////
	//////////////// down
	//coords[0]=scoords[0];
	//coords[1]=scoords[1]+1;
	//if(coords[0]>dims[1])
	//{
	//	coords[0]-=dims[1];
	//}
	//MPI_Cart_rank(cartComm, coords, &did);
	////////////////////////////////
	//////////////// right
	//coords[0]=scoords[0]+1;
	//coords[1]=scoords[1];
	//if(coords[0]>dims[0])
	//{
	//	coords[0]-=dims[0];
	//}
	//MPI_Cart_rank(cartComm, coords, &rid);
	////////////////////////////////
	//////////////// up
	//coords[0]=scoords[0];
	//coords[1]=scoords[1]-1;
	//if(coords[1]<0)
	//{
	//	coords[0]+=dims[1];
	//}
	//MPI_Cart_rank(cartComm, coords, &uid);
	/////////////////////////////////
	MPI_Cart_shift(cartComm,0,1,&uid,&did);
	MPI_Cart_shift(cartComm,1,1,&lid,&rid);
	//set up the matrices in the local process
	int subMatrixSize = L/dims[0];
	 //printf("here0",i);
	fflush(stdout);
	//float A[subMatrixSize*subMatrixSize],B[subMatrixSize*subMatrixSize],C[subMatrixSize*subMatrixSize];
	float *A,*B,*C;
	A= (float*)calloc(subMatrixSize*subMatrixSize, sizeof(float));
	B= (float*)calloc(subMatrixSize*subMatrixSize, sizeof(float));
	C= (float*)calloc(subMatrixSize*subMatrixSize, sizeof(float));
	 //printf("here0.1",i);
	fflush(stdout);
	genMatrices(A,B,C,L/dims[0],bigI,bigJ); //generate the local matrices
	 //printf("proc num:%i; r:%i; l:%i\n",sid,rid,lid);
	
	//as before, I started counting time once the matrices were finished being generated. This will create some discrepancy since only
	//the root process will keep this initial time, but oh well...
	struct timeval start, stop;
	gettimeofday( &start, NULL ); //set start to current time
	printf("%i:%i\n",dims[0],dims[1]);
		fflush(stdout);
	//then shift for setup
	//row matrices first
	for(i = 0; i <bigJ; i++)//shifting the rows left and right
	{
		//MPI_Send(A,subMatrixSize*subMatrixSize,MPI_FLOAT,lid,0,cartComm);
		//MPI_Recv(A,subMatrixSize*subMatrixSize,MPI_FLOAT,rid,0,cartComm,&status);
		//////////////////////////MPI_Sendrecv(A, (subMatrixSize*subMatrixSize), MPI_FLOAT,lid,0,
        //////////////////////////A,(subMatrixSize*subMatrixSize),MPI_FLOAT,rid,0,cartComm,MPI_STATUS_IGNORE);
		MPI_Sendrecv_replace(A,subMatrixSize*subMatrixSize,MPI_FLOAT,lid,0,rid,0,cartComm,&status);
	}
	 //printf("here1",i);
		fflush(stdout);
	for(i = 0; i <bigI; i++)//shifting the cols up and down
	{
		//MPI_Send(B,subMatrixSize*subMatrixSize,MPI_FLOAT,uid,0,cartComm);
		//MPI_Recv(B,subMatrixSize*subMatrixSize,MPI_FLOAT,did,0,cartComm,&status);
		//////////////////////////MPI_Sendrecv(B, (subMatrixSize*subMatrixSize), MPI_FLOAT,lid,0,
        //////////////////////////B,(subMatrixSize*subMatrixSize),MPI_FLOAT,rid,0,cartComm,MPI_STATUS_IGNORE);
		MPI_Sendrecv_replace(B,subMatrixSize*subMatrixSize,MPI_FLOAT,uid,0,did,0,cartComm,&status);
	}
	//after this, setup is complete
	//////////////////////////////////
	//////////////////////////////////////
	
	//with setup complete, it's time for the actual matmul loop
	for(i = 0; i<dims[1];i++)
	{
		printf("Process %i made it to matmul iteration %i\n",sid,i);
		fflush(stdout);
		matmul(subMatrixSize,subMatrixSize,subMatrixSize,A,B,C); //calculate the local submatrix multiply
		//exchange left and right
		//MPI_Send(A,subMatrixSize*subMatrixSize,MPI_FLOAT,lid,0,cartComm);
		//MPI_Recv(A,subMatrixSize*subMatrixSize,MPI_FLOAT,rid,0,cartComm,&status);
		MPI_Sendrecv_replace(A,subMatrixSize*subMatrixSize,MPI_FLOAT,lid,0,rid,0,cartComm,&status);
		//////////////////////////////MPI_Sendrecv(A, (subMatrixSize*subMatrixSize), MPI_FLOAT,lid,0,
        //////////////////////////////A,(subMatrixSize*subMatrixSize),MPI_FLOAT,rid,0,cartComm,MPI_STATUS_IGNORE);
		//exchange up and down
		//MPI_Send(B,subMatrixSize*subMatrixSize,MPI_FLOAT,uid,0,cartComm);
		//MPI_Recv(B,subMatrixSize*subMatrixSize,MPI_FLOAT,did,0,cartComm,&status);
		MPI_Sendrecv_replace(B,subMatrixSize*subMatrixSize,MPI_FLOAT,uid,0,did,0,cartComm,&status);
		//////////////////////////////MPI_Sendrecv(B, (subMatrixSize*subMatrixSize), MPI_FLOAT,lid,0,
        //////////////////////////////B,(subMatrixSize*subMatrixSize),MPI_FLOAT,rid,0,cartComm,MPI_STATUS_IGNORE);
		
		//**for shiggles print out the time here to debug//
		gettimeofday( &stop, NULL ); //set stop to current time
		float elapsed = ( (stop.tv_sec-start.tv_sec) +
			(stop.tv_usec-start.tv_usec)/(float)1000000 ); //calculate seconds of elapsed time

		float flops = ( 2 * (float)L * (float)M * (float)N ) / elapsed; //calculate floating point operations per second using the known problem sized
																	//and the elapsed time

	printf( "L=%d, M=%d, N=%d, elapsed=%g, flops=%g\n",
		L, M, N, elapsed, flops ); //prints problem size, time elapsed, and flops calculated
	}
	
	gettimeofday( &stop, NULL ); //set stop to current time
	float elapsed = ( (stop.tv_sec-start.tv_sec) +
			(stop.tv_usec-start.tv_usec)/(float)1000000 ); //calculate seconds of elapsed time

	float flops = ( 2 * (float)L * (float)M * (float)N ) / elapsed; //calculate floating point operations per second using the known problem sized
																	//and the elapsed time

	printf( "L=%d, M=%d, N=%d, elapsed=%g, flops=%g\n",
		L, M, N, elapsed, flops ); //prints problem size, time elapsed, and flops calculated

	MPI_Finalize();
	//printf("w0t");
	
	

#ifdef DEBUG
	printf( "A:\n" );
	//print matrix A
	for( i=0; i<L; i++ ) //loop over rows of A
	{
	  printf( "%g", A[i*M] );
	  for( j=1; j<M; j++ ) //loop over columns of A
	  {
	    printf( " %g", A[i*M+j] ); //print at Aij
	  }
	  printf( "\n" );
	}

	printf( "B:\n" );
	//print matrix B
	for( j=0; j<M; j++ ) //loop over rows of B
	{
	  printf( "%g", B[j*N] );
	  for( k=1; k<N; k++ ) //loop over columns of B
	  {
	    printf( " %g", B[j*N+k] ); //print at Bjk
	  }
	  printf( "\n" );
	}

	printf( "C:\n" );
	//print matrix C
	for( i=0; i<L; i++ ) //loop over rows of B
	{
	  printf( "%g", C[i*N] );
	  for( k=1; k<N; k++ ) //loop over columns of B
	  {
	    printf( " %g", C[i*N+k] );
	  }
	  printf( "\n" );
	}
#endif
}

///////////////////////////////////////////////////////////////////////////////
// int main( int argc, char *argv[] )
// Author: Dr. Richard A. Goodrum, Ph.D.
// Date:  16September 2017
// Description: Generates two matrices and then calls matmul to multiply them.
// 	Finally, it verifies that the results are correct.
//
// Parameters:
//	l		I/P	int	The first dimension of A and C
//	m		I/P	int	The second dimension of A and  first of B
//	n		I/P	int	The second dimension of B and C
//	A		I/P	float *	The first input matrix
//	B		I/P	float *	The second input matrix
//	C		O/P	float *	The output matrix
//	matmul	O/P	int	Status code
///////////////////////////////////////////////////////////////////////////////
int matmul( int l, int m, int n, float *A, float *B, float *C )
{
	int i, j, k;
	#pragma omp parallel for private(i,k,j)
	for( i=0; i<l; i++ )				// Loop over the rows of A and C.
	{
		//if(i%1000==0)
		//{
		//printf("%i\n",i*n+k);
		//fflush(stdout);
		//}
	  for( k=0; k<n; k++ )				// Loop over the columns of B and C
	  {
	// Initialize the output element for the inner
	// product of row i of A with column j of B				
	    for( j=0; j<m; j++ )				// Loop over the columns of A and C
	    {
			
			C[i*n+k] += A[i*m+j] * B[j*n+k];	// Compute the inner product
			
	    }
	  }
	}
}

///////////////////////////////////////////////////////////////////////////////
// int genMatrices(float *A, float *B,float *C, int subMatrixSize,int bigI, int bigJ)
// Author: Tobias Kroll
// Date:  20 October 2018
// Description: generates the passed in arrays as matrices
//
// Parameters:
//	bigI			I/P	int	The process x lattice location
//	bigJ			I/P	int	The process y lattice location
//	subMatrixSize	I/P	int	The matrix dimensions
//	A				O/P	float *	The first output matrix
//	B				O/P	float *	The second output matrix
//	C				O/P	float *	The output matrix
//	genMatrices		O/P	int	Status code
///////////////////////////////////////////////////////////////////////////////
int genMatrices(float *A, float *B,float *C, int subMatrixSize,int bigI, int bigJ)
{
	int i,j,k;
	//printf("genMatrixhere1",i);
	//fflush(stdout);
	for( i=0; i<subMatrixSize; i++ ) //loops over the rows of A
	{
	  for( j=0; j<subMatrixSize; j++ ) //loops over the columns of A
	  {
		//initialize the specified value of Matrix A (Aij = (i*M+j+1)
		//The result is a matrix with each element increasing by 1 from the next column-wise
	    
		A[i*subMatrixSize+j] = (float) ((i+((bigI+1)*subMatrixSize))*M+((bigJ+1)*subMatrixSize)+j+1);
	    
	  }
	}
	//printf("genMatrixhere2",i);
	//fflush(stdout);
	for( j=0; j<subMatrixSize; j++ ) //loops over the rows of B
	{
	  for( k=0; k<subMatrixSize; k++ ) //loops over the columns of A
	  {
		//initialize the specified value of Matrix B (Bjk = (j*M+k+1))
		//the result is an augmented square upper triangular matrix filled with 1's in the top right
		//of the square, ((augmented with a matrix whose rows increase beginning with 2)).
	    if( ((j+((bigI)*subMatrixSize)) <= k+((bigJ)*subMatrixSize )))
	    {
			B[j*subMatrixSize+k] = 1.0;
		}
	    else
	    {
			B[j*subMatrixSize+k] = 0.0;
	    }
	  }
	}
	//printf("genMatrixhere3",i);
	//fflush(stdout);
	for( j=0; j<subMatrixSize; j++ ) //loops over the rows of B
	{
	  for( k=0; k<subMatrixSize; k++ ) //loops over the columns of A
	  {
		//initialize C as an empty matrices
		C[j*subMatrixSize+k] = 0.0;
	    
	  }
	}
	//printf("genMatrixhere4",i);
	//fflush(stdout);
}





