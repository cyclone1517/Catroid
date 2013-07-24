/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.os.Build;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import android.widget.Spinner;

public class LegoNxtMotorActionBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {
	private static final int SET_SPEED = 30;
	private static final int SET_SPEED_INITIALLY = -70;

	private Project project;
	private LegoNxtMotorActionBrick motorBrick;

	public LegoNxtMotorActionBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		// normally super.setUp should be called first
		// but kept the test failing due to view is null
		// when starting in ScriptActivity
		createProject();
		super.setUp();
	}

	@Smoke
	
	public void testNXTMotorActionBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_motor_action)));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.motor_speed)));

		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, SET_SPEED, "speed", motorBrick);

		String[] motors = getActivity().getResources().getStringArray(R.array.nxt_motor_chooser);
		assertTrue("Spinner items list too short!", motors.length == 4);

		int legoSpinnerIndex = 1;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			legoSpinnerIndex = 0;
		}

		Spinner currentSpinner = solo.getCurrentViews(Spinner.class).get(legoSpinnerIndex);
		solo.pressSpinnerItem(legoSpinnerIndex, 0);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[0], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, 1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[1], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, 1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[2], currentSpinner.getSelectedItem());
		solo.pressSpinnerItem(legoSpinnerIndex, 1);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertEquals("Wrong item in spinner!", motors[3], currentSpinner.getSelectedItem());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		motorBrick = new LegoNxtMotorActionBrick(sprite, LegoNxtMotorActionBrick.Motor.MOTOR_A, SET_SPEED_INITIALLY);

		script.addBrick(motorBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.INSTANCE.setProject(project);
		ProjectManager.INSTANCE.setCurrentSprite(sprite);
		ProjectManager.INSTANCE.setCurrentScript(script);
	}
}
