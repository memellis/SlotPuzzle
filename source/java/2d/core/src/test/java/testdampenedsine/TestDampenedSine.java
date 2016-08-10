package testdampenedsine;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ellzone.slotpuzzle2d.physics.DampenedSine;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;

public class TestDampenedSine {
	private static final int TEST_NUM_REELS = 8;
	private static final int TEST_HEIGHT = 32 * TEST_NUM_REELS;
	private static final int TEST_DAMP_POINT = TEST_HEIGHT * 20;
	private static final int TEST_DAMPEDSINE_GUARD = 900;
	private DampenedSine dampenedSine;
	private boolean dampenedSineEnd;

	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDampenedSine() {
		dampenedSine = new DampenedSine(0, 0, 0, 0, 0, TEST_DAMP_POINT, TEST_HEIGHT, 0);
		dampenedSine.setCallback(new SPPhysicsCallback() {
			public void onEvent(int type, SPPhysicsEvent event) {
				delegateDSCallback(type);
			};
		});
		dampenedSine.setCallbackTriggers(SPPhysicsCallback.END);
		int dampenedSineGuardCount = 0;
		dampenedSineEnd = false;
		while ((!dampenedSineEnd) & (dampenedSineGuardCount++ < TEST_DAMPEDSINE_GUARD)) {
			dampenedSine.update();
		}
		assertTrue(dampenedSineEnd);
	}
	
	@Test
	public void testDampenedSineEndReel() {
		for (int endReel = 0; endReel < TEST_NUM_REELS; endReel++) {	
			dampenedSine = new DampenedSine(0, 0, 0, 0, 0, TEST_DAMP_POINT, TEST_HEIGHT, endReel);
			dampenedSine.setCallback(new SPPhysicsCallback() {
				public void onEvent(int type, SPPhysicsEvent event) {
					delegateDSCallback(type);
				};
			});
			dampenedSine.setCallbackTriggers(SPPhysicsCallback.END);
			int dampenedSineGuardCount = 0;
			dampenedSineEnd = false;
			while ((!dampenedSineEnd) & (dampenedSineGuardCount++ < TEST_DAMPEDSINE_GUARD)) {
				dampenedSine.update();
			}
			assertTrue(dampenedSineEnd);
			assertTrue(((dampenedSine.position.getY() % TEST_HEIGHT) / DampenedSine.SPRITE_SQUARE_SIZE) == endReel);
		}
	}
		
	private void delegateDSCallback(int type) {
		if (type == SPPhysicsCallback.END) {
			dampenedSineEnd = true;
		}
	}
}
