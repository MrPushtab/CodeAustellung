#pragma once
#define GLEW_STATIC
#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <glm/gtx/string_cast.hpp>

#include "Particle.h"
const static float upwardCoeff = 1.0f;
const static float centripetalCoeff = 1.0f;
class WindForce
{
public:
	static glm::vec3 calcForce(glm::vec3 windPos, particle particle);
public:

};