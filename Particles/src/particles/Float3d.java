package particles;

public class Float3d {
	public float x,y,z;
	
	
	public Float3d() {		
		x = 0f;
		y = 0f;
		z = 0f;
	}
	
	public Float3d(Float3d other) {		
		x = other.x;
		y = other.y;
		z = other.z;
	}
	
	public Float3d(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Float3d(int first, int second, int last) {
		this.x = (float)first;
		this.y = (float)second;
		this.z = (float)last;
	}
		
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Float3d sub(Float3d other) {		
		x = x - other.x; 
		y = y - other.y;
		z = z - other.z;
				
		return this;
	}
	
	public Float3d add(Float3d other) {		
		x = x + other.x; 
		y = y + other.y;
		z = z + other.z;
				
		return this;
	}
	
	public Float3d mult(float other) {
		x = x * other; 
		y = y * other;
		z = z * other;
				
		return this;	
	}
	
	public float dot(Float3d other) {
		float result = 0f;
		result = x*other.x + y*other.y + z*other.z;	
				
		return result;
	}
	
	public Float3d cross(Float3d other) {
		x = y * other.z - z * other.y; 
		y = z * other.x - x * other.z; 
		z = x * other.y - y * other.x; 
				
		return this;	
	}
	
	public Float3d invert() {
		x = -x; 
		y = -y;
		z = -z;
				
		return this;
	}
	
	public float mag() {
		float result = 0f;
		result = (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
		return result;
	}
	
	public Float3d unit() {
		float length = 0f;
		length = (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
		x = x /length; 
		y = y /length;
		z = z /length;
		
		return this;
	}
	
	public Float3d clone() {
		Float3d result = new Float3d(x, y, z);
				
		return result;
	}
}
