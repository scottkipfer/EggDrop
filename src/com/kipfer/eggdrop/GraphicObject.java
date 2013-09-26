package com.kipfer.eggdrop;

import android.graphics.Bitmap;

public class GraphicObject {
	

	public Bitmap _bitmap;
	private Coordinates _coordinates;
	private Speed _speed;
	
	public GraphicObject(Bitmap bitmap){
		_bitmap = bitmap;
		_coordinates = new Coordinates();
		_speed = new Speed();
	}
	
	public Speed getSpeed(){
		return _speed;
	}
	
	public Bitmap getGraphic(){
		return _bitmap;
	}
	
	public Coordinates getCoordinates(){
		return _coordinates;
	}
	public void changeGraphic(Bitmap bitmap){
		_bitmap = bitmap;
	}
	
	public class Coordinates{
		private int _x = 100;
		private int _y = 0;
		
		public int getX(){
			return _x + _bitmap.getWidth() /2;
		}
		
		public void setX(int value){
			_x = value - _bitmap.getWidth() /2;
		}
		
		public int getY(){
			return _y + _bitmap.getHeight() /2;
		}
		
		public void setY(int value){
			_y = value - _bitmap.getHeight() / 2;
		}
		
		@Override
		public String toString() {
			return "Coordinates: (" + _x + "/"+ _y +")";
		}	
		
	}
	public class Speed {
		public static final int X_DIRECTION_RIGHT = 1;
		public static final int X_DIRECTION_LEFT = -1;
		public static final int Y_DIRECTION_DOWN = 1;
		public static final int Y_DIRECTION_UP = -1;
		
		private int _x = 20;
		private int _y = 0;
		
		private int _xDirection = X_DIRECTION_RIGHT;
		private int _yDirection = Y_DIRECTION_DOWN;
		
		public int getXDirection() {
			return _xDirection;
		}
		
		public void toggleXDirection(){
			if (_xDirection == X_DIRECTION_RIGHT){
				_xDirection = X_DIRECTION_LEFT;
			} else {
				_xDirection = X_DIRECTION_RIGHT;
			}
		}
		
		public int getYDirection(){
			return _yDirection;
		}
		
		public void setYDirection(int direction){
			_yDirection = direction;
		}
		
		public void toggleYDirection(){
			if (_yDirection == Y_DIRECTION_DOWN){
				_yDirection = Y_DIRECTION_UP;			
			} else {
				_yDirection = Y_DIRECTION_DOWN;
			}
		}
		
		public int getX(){
			return _x;
		}
		
		public void setX(int speed){
			_x = speed;
		}
		
		public int getY(){
			return _y;
		}
		
		public void setY(int speed){
			_y = speed;
		}
		
		@Override
		public String toString() {
			String xDirection;
			if(_xDirection == X_DIRECTION_RIGHT) {
				xDirection = "right";
			} else {
				xDirection = "left";
			}
			return "Speed x: " + _x + "|y: " + _y + " | xDirection: " + xDirection;
		}
	}


}
