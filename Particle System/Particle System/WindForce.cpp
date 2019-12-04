#include "WindForce.h"
#include <iostream>

glm::vec3 WindForce::calcForce(glm::vec3 windPos, particle particle)
{
	glm::vec3 radiusVector = windPos-particle.pos;//the vector pointing to the middle of the wind column
	radiusVector[1] = 0;
	//std::cout << radiusVector[0] << "::" << radiusVector[1] << "::" << radiusVector[2] << "\n";
	float v = glm::length(particle.v);
	float r = glm::length(radiusVector);
	float a = 2.0f;
	radiusVector=glm::normalize(radiusVector);
	//std::cout << radiusVector[0] << "::" << radiusVector[1] << "::" << radiusVector[2] << "\n";
	radiusVector=radiusVector*(a);//this is the centripetal acceleration
	radiusVector[1] += 0.8f;
	glm::vec3 radiusVec = windPos - particle.pos;
	radiusVec[1] = 0;
	float temp2 = radiusVec[0];
	radiusVec[0] = -radiusVec[2];
	radiusVec[2] = temp2;
	radiusVector = radiusVector + radiusVec * 1.0f;
	//std::cout << radiusVector[0] << "::" << radiusVector[1] << "::" << radiusVector[2] << "\n";
	//return { 0.0f,0.1f,0.0f };
	return radiusVector;
}
