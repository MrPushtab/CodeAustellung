#!/bin/bash
#SBATCH -N 16
#SBATCH -p RM
#SBATCH --ntasks-per-node 28
#SBATCH -t 00:15:00
mpirun -np 16 ./matmul.exe