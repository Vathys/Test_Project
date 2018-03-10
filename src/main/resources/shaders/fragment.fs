#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;

struct Attenuation
{
	float constant;
	float linear;
	float exponent;
};

struct PointLight
{
	vec3 colour;
	vec3 position;
	float intensity;
	Attenuation att;
};

struct SpotLight
{
	PointLight pl;
	vec3 coneDirection;
	float cutoff;
};

struct DirectionalLight
{
	vec3 colour;
	vec3 direction;
	float intensity;
};

struct Material
{
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	int hasTexture;
	float reflectance;
};

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight[MAX_POINT_LIGHTS];
uniform SpotLight spotLight[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColours(Material material, vec2 textCoord)
{
	if(material.hasTexture == 1)
	{
		ambientC = texture(texture_sampler, textCoord);
		diffuseC = ambientC;
		specularC = ambientC;
	}
	else
	{
		ambientC = material.ambient;
		diffuseC = material.diffuse;
		specularC = material.specular;
	}
}

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
	vec4 diffuseColour = vec4(0, 0, 0, 0);
	vec4 specColour = vec4(0, 0, 0, 0);
	
	//Diffuse Light
	float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
	diffuseColour = diffuseC * vec4(light_colour, 1.0) * light_intensity * diffuseFactor;
	
	//Specular Light
	vec3 camera_direction = normalize(-position);
	vec3 from_light_dir = -to_light_dir;
	vec3 reflected_light = normalize(reflect(from_light_dir, normal));
	float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
	specularFactor = pow(specularFactor, specularPower);
	specColour = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_colour, 1.0);
	
	return (diffuseColour + specColour);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
	vec4 diffuseColour = vec4(0, 0, 0, 0);
	vec4 specColour = vec4(0, 0, 0, 0);
	
	//Diffuse Light
	vec3 light_direction = light.position - position;
	vec3 to_light_source = normalize(light_direction);
	float diffuseFactor = max(dot(normal, to_light_source), 0.0);
	diffuseColour = diffuseC * vec4(light.colour, 1.0) * light.intensity * diffuseFactor;
	
	//Specular Light
	vec3 camera_direction = normalize(-position);
	vec3 from_light_source = -to_light_source;
	vec3 reflected_light = normalize(reflect(from_light_source, normal));
	float specularFactor = max(dot(camera_direction, from_light_source), 0.0);
	specularFactor = pow(specularFactor, specularPower);
	specColour = specularC * specularFactor * material.reflectance * vec4(light.colour, 1.0);
	
	//Attenuation
	float distance = length(light_direction);
	float attenuationInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
	
	return (diffuseColour + specColour) / attenuationInv;	
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
	return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);	
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal)
{
	vec3 light_direction = light.pl.position - position;
	vec3 to_light_dir = normalize(light_direction);
	vec3 from_light_dir = -to_light_dir;
	float spot_alpha = dot(from_light_dir, normalize(light.coneDirection));
	
	vec4 colour = vec4(0, 0, 0, 0);
	
	if(spot_alpha > light.cutoff){
		colour = calcPointLight(light.pl, position, normal);
		colour *= (1.0 - (1.0 - spot_alpha)/(1.0 - light.cutoff));
	}
	return colour;
}

void main()
{
	setupColours(material, outTexCoord);
	
	vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, mvVertexPos, mvVertexNormal);
	
	for(int i = 0; i < MAX_POINT_LIGHTS; i++)
	{
		if(pointLight[i].intensity > 0)
		{
			diffuseSpecularComp += calcPointLight(pointLight[i], mvVertexPos, mvVertexNormal);
		}
	}
	
	for(int i = 0; i < MAX_SPOT_LIGHTS; i++)
	{
		if(spotLight[i].pl.intensity > 0)
		{
			diffuseSpecularComp += calcSpotLight(spotLight[i], mvVertexPos, mvVertexNormal);
		}
	}
	
	fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}