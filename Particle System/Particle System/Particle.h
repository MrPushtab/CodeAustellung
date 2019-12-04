#pragma once

#define GLEW_STATIC
#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include <glm/glm.hpp>
struct particle
{
	glm::vec3 pos;
	float scale;
	glm::vec3 v; //velocity
	float lifeTime;
	glm::vec4 color;
};
class Particle
{

};