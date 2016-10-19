package springbook.user.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class UserTest {
	User user;
	
	@Before
	public void setup() {
		user = new User();
	}
	
	@Test
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for (Level level : levels) {
			if (level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgreadLevel();
			
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotUpgrade() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if (level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgreadLevel();
		}
		
	}
}
