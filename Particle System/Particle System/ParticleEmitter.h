#pragma once
#include "Particle.h"
#include <iostream>
#include <algorithm>
class ParticleEmitter
{
public:

	ParticleEmitter();
	~ParticleEmitter();

	void init();
	particle createParticle();
	void update(float delta_t);

public:
	glm::vec3 startPos;
	glm::vec3 pos;
	glm::vec3 destination;

	float strength;
};