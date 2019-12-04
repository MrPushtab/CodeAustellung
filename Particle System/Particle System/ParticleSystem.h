#pragma once

#include <iostream>
#include "ParticleEmitter.h"
#include "Particle.h"
#define GLEW_STATIC
#include <GL/glew.h>
#include <GLFW/glfw3.h>

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <glm/gtx/string_cast.hpp>

#include <vector>

#include "WindForce.h"

class ParticleSystem
{
public:
	ParticleSystem();
	~ParticleSystem();
	ParticleEmitter emitter;
	void init();
	void update(float delta_t);

	void replaceParticles();

public:
	std::vector<glm::vec3> particle_pos;
	std::vector<particle> particles;
	std::vector<glm::vec4> particle_color;


	int particleNum;
	int pps;
	int desiredNum;
	//ParticleEmitter* emitter;
};