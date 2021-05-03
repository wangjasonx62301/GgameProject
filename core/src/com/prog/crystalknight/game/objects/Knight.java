package com.prog.crystalknight.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.prog.crystalknight.game.Assets;
import com.prog.crystalknight.util.Constants;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Knight extends AbstractGameObject{
	
	public static final String TAG = Knight.class.getName();
	
	private final float JUMP_TIME_MAX = 0.3f;
	private final float JUMP_TIME_MIN = 0.05f;
	private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;
	
	// animation
	private Animation<TextureRegion> anim_idle;
	private Animation<TextureRegion> anim_walk;
	
	// judging which direction
	public enum VIEW_DIRECTION{
		LEFT, RIGHT
	}
	
	// all state while in air
	public enum JUMP_STATE{
		GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
	}
	
	private TextureRegion regKnight;
	
	public VIEW_DIRECTION viewDirection;
	public JUMP_STATE jumpState;
	public float timeJumping;
	
	public Knight() {
		init();
	}
	
	public void init() {
		
		dimension.set(1.4f, 1.4f);
		regKnight = Assets.instance.knight.knight;
		
		anim_idle = Assets.instance.knight.anim_idle;
		anim_walk = Assets.instance.knight.anim_walk;
		
		setAnimation(anim_idle);
		
		// center the character
		origin.set(dimension.x / 2, dimension.y / 2);
		
		// set bound : collision to rock or crystal
		bounds.set(0, 0, dimension.x, dimension.y);
		
		// set velocity, acceleration....
		friction.set(30.0f, 0.0f);
		acceleration.set(0.0f, -30.0f);
		terminalVelocity.set(4.0f, 3.5f);
		
		// view direction
		viewDirection = VIEW_DIRECTION.RIGHT;
		
		// jump state
		jumpState = JUMP_STATE.FALLING;
		timeJumping = 0;
		
	}

	public void setJumping(boolean jumpKeyPressed) {
		switch(jumpState) {
			case GROUNDED : // standing
				if(jumpKeyPressed) {
					// counting time
					timeJumping = 0;
					jumpState = JUMP_STATE.JUMP_RISING;
				}
				break;
			case JUMP_RISING : // in the air +y
				if(!jumpKeyPressed) {
					jumpState = JUMP_STATE.JUMP_FALLING;
				}
				break;
			case FALLING :
			case JUMP_FALLING : // in the air -y
				
				break;
		}
	}
	
	public void update(float deltaTime) {
		super.update(deltaTime);
		if(velocity.x == 0 && animation == anim_walk) {
			setAnimation(anim_idle);
		}
		if(velocity.x != 0) {
			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
			
			if(animation == anim_idle && velocity.y != 0) {
				setAnimation(anim_walk);
			}
			
		}
	}
	
	@Override
	protected void updateMotionY(float deltaTime) {
		switch(jumpState) {
			case GROUNDED :
				jumpState = JUMP_STATE.FALLING;
				break;
			case JUMP_RISING : // counting jump time
				timeJumping += deltaTime;
				// judge jump time 
				if(timeJumping <= JUMP_TIME_MAX) {
					// still jumping
					velocity.y = terminalVelocity.y ;
				}
				break;
			case FALLING : 
				break;
			case JUMP_FALLING : 
				// counting time
				timeJumping += deltaTime;
				// jump to minimal H when pressed too short
				if(timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) {
					// still jumping
					velocity.y = terminalVelocity.y ;
				}
		}
		if(jumpState != JUMP_STATE.GROUNDED) {
			super.updateMotionY(deltaTime);
		}
		
	}
	
	@Override
	public void render(SpriteBatch batch) {
		
		TextureRegion reg = null;
		
		// draw
		reg = animation.getKeyFrame(stateTime, true);
		batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT, false);
		
	}
	
	
}
