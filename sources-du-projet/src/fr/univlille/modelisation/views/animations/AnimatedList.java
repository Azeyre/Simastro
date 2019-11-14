package fr.univlille.modelisation.views.animations;

public class AnimatedList {

	private Animation last;
	
	public boolean add(int frameDuration, AnimationType type, double... positions) {
		Animation animation = new Animation(frameDuration, type, positions);
		
		if(last == null) {
			last = animation;
		}else  {
			last.next = animation;
		}
				
		return true;
	}
	
	public boolean hasNext() {
		return last.next != null;
	}

	public Animation next(){
		return (last = last.next);
	}
	
	public Animation last() {
		return last;
	}
	
	public boolean isEmpty() {
		return last == null;
	}
	
	public void clear() {
		last = null;
	}
	
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[");
		
		Animation animation = last;
		
		while(animation != null) {
			stringBuilder.append(animation).append(",");
			animation = animation.next;
		}
		
		return stringBuilder.append("]").toString();
	}
}
