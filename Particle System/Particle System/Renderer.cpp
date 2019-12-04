#include "Renderer.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

Camera* Renderer::m_camera = new Camera();

Lighting* Renderer::m_lightings = new Lighting();


nanogui::Screen* Renderer::m_nanogui_screen = nullptr;

bool Renderer::keys[1024];

Renderer::Renderer()
{
}


Renderer::~Renderer()
{
}

void Renderer::nanogui_init(GLFWwindow* window)
{
	m_nanogui_screen = new nanogui::Screen();
	m_nanogui_screen->initialize(window, true);

	try
	{
		glViewport(0, 0, m_camera->width, m_camera->height);
	}
	catch (std::runtime_error e)
	{
	}

	//glfwSwapInterval(0);
	//glfwSwapBuffers(window);

	// Create nanogui gui
	nanogui::FormHelper *gui_1 = new nanogui::FormHelper(m_nanogui_screen);
	nanogui::ref<nanogui::Window> nanoguiWindow_1 = gui_1->addWindow(Eigen::Vector2i(0, 0), "Nanogui control bar_1");

	//screen->setPosition(Eigen::Vector2i(-width/2 + 200, -height/2 + 300));

	gui_1->addGroup("Camera Position");
	static auto camera_x_widget = gui_1->addVariable("X", m_camera->position[0]);
	static auto camera_y_widget = gui_1->addVariable("Y", m_camera->position[1]);
	static auto camera_z_widget = gui_1->addVariable("Z", m_camera->position[2]);

	gui_1->addButton("Reset Camera", []() {
		m_camera->reset();
		camera_x_widget->setValue(m_camera->position[0]);
		camera_y_widget->setValue(m_camera->position[1]);
		camera_z_widget->setValue(m_camera->position[2]);
	});




	m_nanogui_screen->setVisible(true);
	m_nanogui_screen->performLayout();

	glfwSetCursorPosCallback(window,
		[](GLFWwindow *window, double x, double y) {
		m_nanogui_screen->cursorPosCallbackEvent(x, y);
	}
	);

	glfwSetMouseButtonCallback(window,
		[](GLFWwindow *, int button, int action, int modifiers) {
		m_nanogui_screen->mouseButtonCallbackEvent(button, action, modifiers);
	}
	);

	glfwSetKeyCallback(window,
		[](GLFWwindow *window, int key, int scancode, int action, int mods) {
		//screen->keyCallbackEvent(key, scancode, action, mods);

		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)
			glfwSetWindowShouldClose(window, GL_TRUE);
		if (key >= 0 && key < 1024)
		{
			if (action == GLFW_PRESS)
				keys[key] = true;
			else if (action == GLFW_RELEASE)
				keys[key] = false;
		}
		camera_x_widget->setValue(m_camera->position[0]);
		camera_y_widget->setValue(m_camera->position[1]);
		camera_z_widget->setValue(m_camera->position[2]);
	}
	);

	glfwSetCharCallback(window,
		[](GLFWwindow *, unsigned int codepoint) {
		m_nanogui_screen->charCallbackEvent(codepoint);
	}
	);

	glfwSetDropCallback(window,
		[](GLFWwindow *, int count, const char **filenames) {
		m_nanogui_screen->dropCallbackEvent(count, filenames);
	}
	);

	glfwSetScrollCallback(window,
		[](GLFWwindow *, double x, double y) {
		m_nanogui_screen->scrollCallbackEvent(x, y);
		//m_camera->ProcessMouseScroll(y);
	}
	);

	glfwSetFramebufferSizeCallback(window,
		[](GLFWwindow *, int width, int height) {
		m_nanogui_screen->resizeCallbackEvent(width, height);
	}
	);

}

void Renderer::init()
{
	glfwInit();
	// Set all the required options for GLFW
	glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
	glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

#if defined(__APPLE__)
	glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
#endif

	m_camera->init();

	// Create a GLFWwindow object that we can use for GLFW's functions
	this->m_window = glfwCreateWindow(m_camera->width, m_camera->height, "Assignment 5", nullptr, nullptr);
	glfwMakeContextCurrent(this->m_window);


	m_lightings->init();

	glewExperimental = GL_TRUE;
	glewInit();
	nanogui_init(this->m_window);
	Renderer::m_particleSystem = new ParticleSystem();
	m_particleSystem->init();
}

void Renderer::display(GLFWwindow* window)
{
	Shader m_shader = Shader("./shader/basic.vert", "./shader/basic.frag");

	// Main frame while loop
	while (!glfwWindowShouldClose(window))
	{

		m_particleSystem->update(delta_time);

		glfwPollEvents();

		if (is_scean_reset) {
			scean_reset();
			is_scean_reset = false;
		}

		camera_move();

		m_shader.use();

		setup_uniform_values(m_shader);

		draw_scene(m_shader);

		m_nanogui_screen->drawWidgets();

		// Swap the screen buffers
		glfwSwapBuffers(window);
	}

	// Terminate GLFW, clearing any resources allocated by GLFW.
	glfwTerminate();
	return;
}

void Renderer::run()
{
	init();
	display(this->m_window);
}

void Renderer::load_models()
{
	obj_list.clear();
	Object cube_object("./objs/cube.obj");
	cube_object.obj_color = glm::vec4(1.0, 1.0, 0.0, 1.0);
	cube_object.obj_name = "cube";

	Object plane_object("./objs/plane.obj");
	plane_object.obj_color = glm::vec4(0.5, 0.5, 0.5, 1.0);
	plane_object.obj_name = "plane";

	Object arrow_object("./objs/arrow.obj");
	arrow_object.obj_name = "axis_arrow";


	bind_vaovbo(cube_object);
	bind_vaovbo(plane_object);
	bind_vaovbo(arrow_object);

	// Here we only load one model
	obj_list.push_back(cube_object);
	obj_list.push_back(plane_object);
	obj_list.push_back(arrow_object);
}

void Renderer::draw_scene(Shader& shader)
{
	// Set up some basic parameters
	glClearColor(background_color[0], background_color[1], background_color[2], background_color[3]);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LESS);

	glEnable(GL_CULL_FACE);
	glCullFace(GL_FRONT);
	glFrontFace(GL_CW);

	glm::mat4 world_identity_obj_mat = glm::mat4(1.0f);
	draw_axis(shader, world_identity_obj_mat);
	draw_plane(shader);
	draw_particles(shader, m_particleSystem);

}

void Renderer::camera_move()
{
	float current_frame = glfwGetTime();
	delta_time = current_frame - last_frame;
	last_frame = current_frame;
	// Camera controls
	if (keys[GLFW_KEY_W])
		m_camera->process_keyboard(FORWARD, delta_time);
	if (keys[GLFW_KEY_S])
		m_camera->process_keyboard(BACKWARD, delta_time);
	if (keys[GLFW_KEY_A])
		m_camera->process_keyboard(LEFT, delta_time);
	if (keys[GLFW_KEY_D])
		m_camera->process_keyboard(RIGHT, delta_time);
	if (keys[GLFW_KEY_Q])
		m_camera->process_keyboard(UP, delta_time);
	if (keys[GLFW_KEY_E])
		m_camera->process_keyboard(DOWN, delta_time);
	if (keys[GLFW_KEY_I])
		m_camera->process_keyboard(ROTATE_X_UP, delta_time);
	if (keys[GLFW_KEY_K])
		m_camera->process_keyboard(ROTATE_X_DOWN, delta_time);
	if (keys[GLFW_KEY_J])
		m_camera->process_keyboard(ROTATE_Y_UP, delta_time);
	if (keys[GLFW_KEY_L])
		m_camera->process_keyboard(ROTATE_Y_DOWN, delta_time);
	if (keys[GLFW_KEY_U])
		m_camera->process_keyboard(ROTATE_Z_UP, delta_time);
	if (keys[GLFW_KEY_O])
		m_camera->process_keyboard(ROTATE_Z_DOWN, delta_time);

}

void Renderer::draw_object(Shader& shader, Object& object)
{
	glBindVertexArray(object.vao);

	glUniform3f(glGetUniformLocation(shader.program, "m_object.object_color"), object.obj_color[0], object.obj_color[1], object.obj_color[2]);
	glUniform1f(glGetUniformLocation(shader.program, "m_object.shininess"), object.shininess);

	if (object.m_render_type == RENDER_TRIANGLES)
	{
		if (object.m_obj_type == OBJ_POINTS)
		{
			std::cout << "Error: Cannot render triangles if input obj type is point\n";
			return;
		}
		if (object.m_obj_type == OBJ_TRIANGLES)
		{
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			glDrawArrays(GL_TRIANGLES, 0, object.vao_vertices.size());
		}
	}

	if (object.m_render_type == RENDER_LINES)
	{
		glLineWidth(10.0);
		if (object.m_obj_type == OBJ_POINTS)
		{
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			glDrawArrays(GL_LINE_LOOP, 0, object.vao_vertices.size());
		}
		if (object.m_obj_type == OBJ_TRIANGLES)
		{
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			glDrawArrays(GL_TRIANGLES, 0, object.vao_vertices.size());
		}
	}

	if (object.m_obj_type == RENDER_POINTS)
	{
		glPolygonMode(GL_FRONT_AND_BACK, GL_POINTS);
		glDrawArrays(GL_POINTS, 0, object.vao_vertices.size());
	}
	glBindVertexArray(0);
	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
}

void Renderer::draw_axis(Shader& shader, const glm::mat4 axis_obj_mat)
{
	// You can always see the arrow
	glDepthFunc(GL_ALWAYS);
	// Get arrow obj
	Object *arrow_obj = nullptr;
	for (unsigned int i = 0; i < obj_list.size(); i++)
	{
		if (obj_list[i].obj_name == "axis_arrow") {
			arrow_obj = &obj_list[i];
		}
	}

	if (arrow_obj == nullptr)
		return;

	// Draw main axis
	arrow_obj->obj_mat = axis_obj_mat;
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(arrow_obj->obj_mat));
	arrow_obj->obj_color = glm::vec4(1, 0, 0, 1);
	draw_object(shader, *arrow_obj);

	arrow_obj->obj_mat = axis_obj_mat;
	arrow_obj->obj_mat = glm::rotate(arrow_obj->obj_mat, glm::radians(90.0f), glm::vec3(0, 0, 1));
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(arrow_obj->obj_mat));
	arrow_obj->obj_color = glm::vec4(0, 1, 0, 1);
	draw_object(shader, *arrow_obj);

	arrow_obj->obj_mat = axis_obj_mat;
	arrow_obj->obj_mat = glm::rotate(arrow_obj->obj_mat, glm::radians(-90.0f), glm::vec3(0, 1, 0));
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(arrow_obj->obj_mat));
	arrow_obj->obj_color = glm::vec4(0, 0, 1, 1);
	draw_object(shader, *arrow_obj);
	glDepthFunc(GL_LESS);
}

void Renderer::draw_plane(Shader& shader)
{
	Object *plane_obj = nullptr;
	for (unsigned int i = 0; i < obj_list.size(); i++)
	{
		if (obj_list[i].obj_name == "plane") {
			plane_obj = &obj_list[i];
		}
	}
	if (plane_obj == nullptr)
		return;

	plane_obj->obj_mat = glm::mat4(1.0f);
	plane_obj->obj_mat = glm::scale(plane_obj->obj_mat, glm::vec3(10, 10, 10));
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(plane_obj->obj_mat));
	draw_object(shader, *plane_obj);
}


/*
void Renderer::draw_bones(Shader& shader, Bone_Animation* m_bone_animation)
{
	Object *bone_obj = nullptr;
	for (unsigned int i = 0; i < obj_list.size(); i++)
	{
		if (obj_list[i].obj_name == "cube") {
			bone_obj = &obj_list[i];
		}
	}
	if (bone_obj == nullptr)
		return;

	m_bone_animation->update(delta_time);

	//Drawing the targeted end effector position
	glm::mat4 targetPos = glm::mat4(1.0f);
	targetPos = glm::translate(targetPos, m_bone_animation->target_pos);
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(targetPos));
	bone_obj->obj_color = { 0.0f, 0.7f, 0.0f, 1.0f };
	draw_object(shader, *bone_obj);



	// Draw root bone
	glm::mat4 root_bone_obj_mat = glm::mat4(1.0f);
	root_bone_obj_mat = glm::translate(root_bone_obj_mat, m_bone_animation->root_position);
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(root_bone_obj_mat));
	bone_obj->obj_color = m_bone_animation->colors[0];
	draw_object(shader, *bone_obj);
	root_bone_obj_mat = glm::translate(root_bone_obj_mat, m_bone_animation->pivot_vector[0]);

	//////////////////////////////////////////////////////
	// Draw 1st bone
	glm::mat4 first_bone_obj_mat = glm::mat4(1.0f);
	/////ROTATE
	first_bone_obj_mat = m_bone_animation->fullRotate(m_bone_animation->rotation_degree_vector[1])*first_bone_obj_mat;

	/////TRANSLATE
	first_bone_obj_mat = glm::translate(first_bone_obj_mat, m_bone_animation->pivot_vector[1]);

	/////save the unscaled object mat
	glm::mat4 temp_obj_mat = first_bone_obj_mat;
	/////SCALE
	first_bone_obj_mat = glm::scale(first_bone_obj_mat, m_bone_animation->scale_vector[1]);
	/////Multiply by last object matrix
	first_bone_obj_mat = root_bone_obj_mat * first_bone_obj_mat;
	temp_obj_mat = root_bone_obj_mat * temp_obj_mat;
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(first_bone_obj_mat));
	bone_obj->obj_color = m_bone_animation->colors[1];
	draw_object(shader, *bone_obj);
	first_bone_obj_mat = temp_obj_mat;
	/////After drawing, do another translation by half length, so the next bone can get the right pivot
	first_bone_obj_mat = glm::translate(first_bone_obj_mat, m_bone_animation->pivot_vector[1]);
	/////also undo scale
	//first_bone_obj_mat = glm::scale(first_bone_obj_mat, { 2,0.25,2 });


	//////////////////////////////////
	// Draw 2nd bone
	glm::mat4 second_bone_obj_mat = glm::mat4(1.0f);
	///ROT
	second_bone_obj_mat = m_bone_animation->fullRotate(m_bone_animation->rotation_degree_vector[2])*second_bone_obj_mat;
	///TRANS
	second_bone_obj_mat = glm::translate(second_bone_obj_mat, m_bone_animation->pivot_vector[2]);
	///SCALE
	temp_obj_mat = second_bone_obj_mat;
	second_bone_obj_mat = glm::scale(second_bone_obj_mat, m_bone_animation->scale_vector[2]);
	///Mult by last matrix
	second_bone_obj_mat = first_bone_obj_mat * second_bone_obj_mat;
	temp_obj_mat = first_bone_obj_mat * temp_obj_mat;
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(second_bone_obj_mat));
	bone_obj->obj_color = m_bone_animation->colors[2];
	draw_object(shader, *bone_obj);
	second_bone_obj_mat = temp_obj_mat;
	//after cleanup, another translation by halflength
	second_bone_obj_mat = glm::translate(second_bone_obj_mat, m_bone_animation->pivot_vector[2]);


	// Draw 3rd bone
	glm::mat4 third_bone_obj_mat = glm::mat4(1.0f);

	///ROT
	third_bone_obj_mat = m_bone_animation->fullRotate(m_bone_animation->rotation_degree_vector[3])*third_bone_obj_mat;
	///TRANS
	third_bone_obj_mat = glm::translate(third_bone_obj_mat, m_bone_animation->pivot_vector[3]);

	///SCALE
	temp_obj_mat = third_bone_obj_mat;
	third_bone_obj_mat = glm::scale(third_bone_obj_mat, m_bone_animation->scale_vector[3]);

	/**
	for (int i = 2; i > 0; i--)
	{
		third_bone_obj_mat = m_bone_animation->fullRotate(m_bone_animation->rotation_degree_vector[i])*third_bone_obj_mat;
		third_bone_obj_mat = glm::translate(third_bone_obj_mat, m_bone_animation->pivot_vector[i]);
		//third_bone_obj_mat = glm::translate(third_bone_obj_mat, m_bone_animation->pivot_vector[i]);
	}
	third_bone_obj_mat = root_bone_obj_mat * third_bone_obj_mat;
	third_bone_obj_mat = second_bone_obj_mat * third_bone_obj_mat;
	temp_obj_mat = second_bone_obj_mat * temp_obj_mat;
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(third_bone_obj_mat));
	bone_obj->obj_color = m_bone_animation->colors[3];
	draw_object(shader, *bone_obj);
	third_bone_obj_mat = temp_obj_mat;
	third_bone_obj_mat = glm::translate(third_bone_obj_mat, m_bone_animation->pivot_vector[3]);

	//Drawing the current end effector position
	targetPos = glm::mat4(1.0f);
	targetPos = glm::scale(targetPos, { 0.25,0.25,0.25 });
	targetPos = third_bone_obj_mat * targetPos;
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(targetPos));
	bone_obj->obj_color = { 0.0f, 0.0f, 0.7f, 1.0f };
	draw_object(shader, *bone_obj);

	glm::vec4 currRealEPos = targetPos * glm::vec4(m_bone_animation->start_e_pos, 1.0f);
	m_bone_animation->current_e_pos = { currRealEPos[0],currRealEPos[1],currRealEPos[2] };
	std::cout << "x::" << currRealEPos[0] << "y::" << currRealEPos[1] << "z::" << currRealEPos[2] << "\n";
}*/

void Renderer::draw_particles(Shader& shader, ParticleSystem* m_particleSystem)
{
	Object *particle_obj = nullptr;
	for (unsigned int i = 0; i < obj_list.size(); i++)
	{
		if (obj_list[i].obj_name == "cube") {
			particle_obj = &obj_list[i];
		}
	}
	if (particle_obj == nullptr)
		return;
	int i = 0;
	for (i; i < m_particleSystem->particle_pos.size(); i++)
	{
		glm::mat4 targetPos = glm::mat4(1.0f);
		targetPos = glm::translate(targetPos, m_particleSystem->particle_pos[i]);
		glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(targetPos));
		particle_obj->obj_color = m_particleSystem->particle_color[i];
		draw_object(shader, *particle_obj);
	}
	glm::mat4 targetPos = glm::mat4(1.0f);
	targetPos = glm::translate(targetPos, m_particleSystem->emitter.destination);
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "model"), 1, GL_FALSE, glm::value_ptr(targetPos));
	particle_obj->obj_color = { 0.0f,1.0f,0.0f,1.0f };
	draw_object(shader, *particle_obj);
}


void Renderer::bind_vaovbo(Object &cur_obj)
{
	glGenVertexArrays(1, &cur_obj.vao);
	glGenBuffers(1, &cur_obj.vbo);

	glBindVertexArray(cur_obj.vao);

	glBindBuffer(GL_ARRAY_BUFFER, cur_obj.vbo);
	glBufferData(GL_ARRAY_BUFFER, cur_obj.vao_vertices.size() * sizeof(Object::Vertex), &cur_obj.vao_vertices[0], GL_STATIC_DRAW);

	// Vertex Positions
	glEnableVertexAttribArray(0);
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, sizeof(Object::Vertex), (GLvoid*)0);
	// Vertex Normals
	glEnableVertexAttribArray(1);
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, sizeof(Object::Vertex), (GLvoid*)offsetof(Object::Vertex, Normal));
	// Vertex Texture Coords
	glEnableVertexAttribArray(2);
	glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, sizeof(Object::Vertex), (GLvoid*)offsetof(Object::Vertex, TexCoords));

	glBindVertexArray(0);
}

void Renderer::setup_uniform_values(Shader& shader)
{
	// Camera uniform values
	glUniform3f(glGetUniformLocation(shader.program, "camera_pos"), m_camera->position.x, m_camera->position.y, m_camera->position.z);

	glUniformMatrix4fv(glGetUniformLocation(shader.program, "projection"), 1, GL_FALSE, glm::value_ptr(m_camera->get_projection_mat()));
	glUniformMatrix4fv(glGetUniformLocation(shader.program, "view"), 1, GL_FALSE, glm::value_ptr(m_camera->get_view_mat()));

	// Light uniform values
	glUniform1i(glGetUniformLocation(shader.program, "dir_light.status"), m_lightings->direction_light.status);
	glUniform3f(glGetUniformLocation(shader.program, "dir_light.direction"), m_lightings->direction_light.direction[0], m_lightings->direction_light.direction[1], m_lightings->direction_light.direction[2]);
	glUniform3f(glGetUniformLocation(shader.program, "dir_light.ambient"), m_lightings->direction_light.ambient[0], m_lightings->direction_light.ambient[1], m_lightings->direction_light.ambient[2]);
	glUniform3f(glGetUniformLocation(shader.program, "dir_light.diffuse"), m_lightings->direction_light.diffuse[0], m_lightings->direction_light.diffuse[1], m_lightings->direction_light.diffuse[2]);
	glUniform3f(glGetUniformLocation(shader.program, "dir_light.specular"), m_lightings->direction_light.specular[0], m_lightings->direction_light.specular[1], m_lightings->direction_light.specular[2]);

	// Set current point light as camera's position
	m_lightings->point_light.position = m_camera->position;
	glUniform1i(glGetUniformLocation(shader.program, "point_light.status"), m_lightings->point_light.status);
	glUniform3f(glGetUniformLocation(shader.program, "point_light.position"), m_lightings->point_light.position[0], m_lightings->point_light.position[1], m_lightings->point_light.position[2]);
	glUniform3f(glGetUniformLocation(shader.program, "point_light.ambient"), m_lightings->point_light.ambient[0], m_lightings->point_light.ambient[1], m_lightings->point_light.ambient[2]);
	glUniform3f(glGetUniformLocation(shader.program, "point_light.diffuse"), m_lightings->point_light.diffuse[0], m_lightings->point_light.diffuse[1], m_lightings->point_light.diffuse[2]);
	glUniform3f(glGetUniformLocation(shader.program, "point_light.specular"), m_lightings->point_light.specular[0], m_lightings->point_light.specular[1], m_lightings->point_light.specular[2]);
	glUniform1f(glGetUniformLocation(shader.program, "point_light.constant"), m_lightings->point_light.constant);
	glUniform1f(glGetUniformLocation(shader.program, "point_light.linear"), m_lightings->point_light.linear);
	glUniform1f(glGetUniformLocation(shader.program, "point_light.quadratic"), m_lightings->point_light.quadratic);
}

void Renderer::scean_reset()
{
	load_models();
	m_camera->reset();
}