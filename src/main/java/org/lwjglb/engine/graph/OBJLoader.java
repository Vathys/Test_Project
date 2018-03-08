package org.lwjglb.engine.graph;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjglb.engine.Utils;

public class OBJLoader {
	public static Mesh loadMesh(String filename) throws Exception {
		List<String> lines = Utils.readAllLines(filename);
		
		List<Vector3f> vertices = new ArrayList<>();
		List<Vector2f> textures = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Face> faces = new ArrayList<>();
		
		for(String line : lines) {
			String[] tokens = line.split("\\s+");
			switch(tokens[0]) {
			case "v":
				// Geometric Vertex
				Vector3f vec3f = new Vector3f(
					Float.parseFloat(tokens[1]),
					Float.parseFloat(tokens[2]),
					Float.parseFloat(tokens[3])
				);
				vertices.add(vec3f);
				break;
			case "vt":
				//Texture Coordinates
				Vector2f vec2f = new Vector2f(
					Float.parseFloat(tokens[1]),
					Float.parseFloat(tokens[2])
				);
				textures.add(vec2f);
				break;
			case "vn":
				//Normal Vertex
				Vector3f vec3fNorm = new Vector3f(
					Float.parseFloat(tokens[1]),
					Float.parseFloat(tokens[2]),
					Float.parseFloat(tokens[3])
				);
				normals.add(vec3fNorm);
				break;
			case "f":
				Face face = new Face(tokens[1], tokens[2], tokens[3]);
				faces.add(face);
				break;
			default:
				break;
			}
		}
		return reorderLists(vertices, textures, normals, faces);
	}
	
	private static Mesh reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList, List<Vector3f> normList, List<Face> faceList) {
		List<Integer> indices = new ArrayList<>();
		
		float[] posArray = new float[posList.size() * 3];
		int i = 0;
		for(Vector3f pos : posList) {
			posArray[i * 3] = pos.x;
			posArray[(i * 3) + 1] = pos.y;
			posArray[(i * 3) + 2] = pos.z;
			i++;
		}
		float[] textCoordArray = new float[posList.size() * 2];
		float[] normArray = new float[posList.size() * 3];
		
		for (Face face : faceList) {
			IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
			for(IdxGroup indValue : faceVertexIndices) {
				processFaceValue(indValue, textCoordList, normList, indices, textCoordArray, normArray);
			}
		}
		int[] indicesArray = new int[indices.size()];
		indicesArray = indices.stream().mapToInt((Integer v) -> v).toArray();
		Mesh mesh = new Mesh(posArray, textCoordArray, normArray, indicesArray);
		return mesh;
	}
	
	private static void processFaceValue(IdxGroup indices, List<Vector2f> textCoordList, List<Vector3f> normList, List<Integer> indicesList, float[] textCoordArray, float[] normArray) {
		int posIndex = indices.idxPos;
		indicesList.add(posIndex);
		
		if(indices.idxTextCoord >= 0) {
			Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
			textCoordArray[posIndex * 2] = textCoord.x;
			textCoordArray[(posIndex * 2) + 1] = 1 - textCoord.y;
		}
		if(indices.idxVecNormal >= 0) {
			Vector3f vecNorm = normList.get(indices.idxVecNormal);
			normArray[posIndex * 3] = vecNorm.x;
			normArray[(posIndex * 3) + 1] = vecNorm.y;
			normArray[(posIndex * 3) + 2] = vecNorm.z;
		}
	}
	
	protected static class Face {
		
        private IdxGroup[] idxGroups;
        
        public Face (String v1, String v2, String v3) {
        	idxGroups = new IdxGroup[3];
        	
        	idxGroups[0] = parseLine(v1);
        	idxGroups[1] = parseLine(v2);
        	idxGroups[2] = parseLine(v3);
        }
        
        private IdxGroup parseLine(String line) {
        	IdxGroup idxGroup = new IdxGroup();
        	
        	String[] lineTokens = line.split("/");
        	int length = line.length();
        	idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
        	if(length > 1) {
        		String textCoord = lineTokens[1];
        		idxGroup.idxTextCoord = (textCoord.length() > 0) ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
        		if(length > 2) {
        			idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
        		}
        	}
        	
        	return idxGroup;
        }
        
        public IdxGroup[] getFaceVertexIndices() {
        	return idxGroups;
        }
	}
	
	protected static class IdxGroup {
		public static final int NO_VALUE = -1;
		public int idxPos;
		public int idxTextCoord;
		public int idxVecNormal;
		
		public IdxGroup() {
			idxPos = NO_VALUE;
			idxTextCoord = NO_VALUE;
			idxVecNormal = NO_VALUE;
		}
	}
}
