#include "ParticleSystem.h"
#include "ParticleEmitter.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

ParticleSystem::ParticleSystem()
{
}

ParticleSystem::~ParticleSystem()
{
}

void ParticleSystem::init()
{
	/*
	for (int i = 0; i < 50000; i++)
	{
		particle temp;
		temp.pos = { (float)(rand()) / (float)(RAND_MAX / 20.0),(float)(rand()) / (float)(RAND_MAX / 20.0),(float)(rand()) / (float)(RAND_MAX / 20.0), };
		temp.scale = 0.04;
		temp.v = { 0.5 - (float)(rand()) / (float)(RAND_MAX),0.5 - (float)(rand()) / (float)(RAND_MAX),0.5 - (float)(rand()) / (float)(RAND_MAX), };
		temp.lifeTime = (float)(rand()) / (float)(RAND_MAX / 1000);
		particles.push_back(temp);
		particle_pos.push_back(temp.pos);
	}*/
	emitter.init();
	pps = 200;
	particleNum = 0;
	desiredNum = 20000;
}

void ParticleSystem::update(float delta_t)
{
	emitter.update(delta_t);
	int newParticleCount = 0;
	for (int i = 0; i < particles.size(); i++)
	{
		particles[i].lifeTime -= delta_t;
		if (particles[i].lifeTime < 0 || glm::length(particles[i].pos-emitter.pos)>40.0f)
		{
			particles.erase(particles.begin()+i);
			particle_pos.erase(particle_pos.begin() + i);
			particle_color.erase(particle_color.begin() + i);
			newParticleCount++;
			i--;
			continue;
		}
		//particles[i].v += (WindForce::calcForce(emitter.pos, particles[i])*delta_t);
		glm::vec3 temp = particles[i].pos;
		particles[i].pos = particles[i].pos + (delta_t*particles[i].v/2.0f);
		particles[i].v += (WindForce::calcForce(emitter.pos, particles[i])*delta_t);
		particles[i].pos = temp+particles[i].v*delta_t;
		particle_pos[i] = particles[i].pos;
		//std::cout << particles[i].v[0] << "::" << particles[i].v[1] << "::" << particles[i].v[2] << "\n";
	}
	particleNum -= newParticleCount;
	replaceParticles();
}

void ParticleSystem::replaceParticles()
{
	for (int i = 0; i < pps*emitter.strength && (particleNum < desiredNum); i++)
	{
		particle temp = emitter.createParticle();
		particles.push_back(emitter.createParticle());
		particle_pos.push_back(temp.pos);
		particle_color.push_back(temp.color);
		particleNum++;

		//std::cout << (temp.pos-emitter.pos)[0] <<"\n";
	}
	
}
