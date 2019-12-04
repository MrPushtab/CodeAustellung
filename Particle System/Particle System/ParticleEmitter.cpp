#include "ParticleEmitter.h"
ParticleEmitter::ParticleEmitter()
{
}

ParticleEmitter::~ParticleEmitter()
{
}

void ParticleEmitter::init()
{
	startPos = { 5.0f,1.0f,5.0f };
	pos = { 5.0f,1.0f,5.0f };
	destination = { 2.0f,-0.5f,2.0f };
	strength = 0.9f;
}

particle ParticleEmitter::createParticle()
{
	particle temp;
	temp.pos =  { pos[0] +0.5f- (float)(rand()) / (float)(RAND_MAX / 1.0f),std::max(pos[1],0.0f), pos[2] + 0.5f - (float)(rand()) / (float)(RAND_MAX / 1.0f) };;
	temp.scale = 0.04;
	//temp.v = { 0.5 - (float)(rand()) / (float)(RAND_MAX),0.5 - (float)(rand()) / (float)(RAND_MAX),0.5 - (float)(rand()) / (float)(RAND_MAX), };
	glm::vec3 radiusVec = pos - temp.pos;
	radiusVec[1] = 0;
	float temp2 = radiusVec[0];
	radiusVec[0] = -radiusVec[2];
	radiusVec[2] = temp2;
	temp.v = radiusVec;
	temp.v = glm::normalize(temp.v);
	temp.v[1] = (float)(rand()) / (float)(RAND_MAX / 2.0f);
	temp.lifeTime = (float)(rand()) / (float)(RAND_MAX / 60.0f);
	if (pos[1] > 1.0f)
	{
		float tempF = (float)(rand()) / (float)(RAND_MAX / 0.3f);
		temp.color = { 0.5f+tempF,0.5f + tempF,0.5f + tempF,1.0f };
	}
	else if(pos[1]<0.0f)
	{
		temp.color = { 0.25f,0.2f + (float)(rand()) / (float)(RAND_MAX / 0.05f),0.0f,1.0f };
	}
	else
	{
		float tempF = (float)(rand()) / (float)(RAND_MAX / 0.3f);
		temp.color = { (0.5f + tempF)*pos[1]+ 0.25f*(1.0f-pos[1]),(0.5f + tempF) * pos[1] + 0.25f*(1.0f - pos[1]),0.0f,1.0f };
	}
	temp.color[2] = temp.color[1];
	return temp;
}

void ParticleEmitter::update(float delta_t)
{
	glm::vec3 movVec = destination - startPos;
	pos = pos + (movVec*delta_t) / 10.0f;
	if (glm::length(pos - destination) < 0.1)
	{
		startPos = pos;
		destination = { 10.0 - (float)(rand()) / (float)(RAND_MAX / 20.0f),3.0f- (float)(rand()) / (float)(RAND_MAX / 5.0f), 10.0 - (float)(rand()) / (float)(RAND_MAX / 20.0f) };
		strength = (float)(rand()) / (float)(RAND_MAX);
	}
}
