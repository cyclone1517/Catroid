/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

@RunWith(AndroidJUnit4.class)
public class VariableBrickTest {
	private int setBrickPosition;
	private int changeBrickPosition;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		setBrickPosition = 1;
		changeBrickPosition = 2;
		Script script = BrickTestUtils.createProjectAndGetStartScript("variableBricksTest");
		script.addBrick(new SetVariableBrick());
		script.addBrick(new ChangeVariableBrick());
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testVariableBricks() {
		int intToChange = 5;
		double doubleToChange = 10.6;
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(setBrickPosition).checkShowsText(R.string.brick_set_variable);
		onBrickAtPosition(changeBrickPosition).checkShowsText(R.string.brick_change_variable);

		onBrickAtPosition(setBrickPosition).onFormulaTextField(R.id.brick_set_variable_edit_text)
				.performEnterNumber(intToChange)
				.checkShowsNumber(intToChange);

		onBrickAtPosition(setBrickPosition).onFormulaTextField(R.id.brick_set_variable_edit_text)
				.performEnterNumber(doubleToChange)
				.checkShowsNumber(doubleToChange);

		onBrickAtPosition(changeBrickPosition).onFormulaTextField(R.id.brick_change_variable_edit_text)
				.performEnterNumber(intToChange)
				.checkShowsNumber(intToChange);

		onBrickAtPosition(changeBrickPosition).onFormulaTextField(R.id.brick_change_variable_edit_text)
				.performEnterNumber(doubleToChange)
				.checkShowsNumber(doubleToChange);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	@Flaky
	public void testCreatingNewVariable() {
		final String variableName = "testVariable";
		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(variableName)
				.checkShowsText(variableName);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	@Flaky
	public void testNewVariableCanceling() {
		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.perform(click());

		onView(withText(R.string.new_option))
				.perform(click());

		closeSoftKeyboard();
		onView(withText(R.string.cancel))
				.perform(click());
		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.checkShowsText(R.string.new_option);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testAfterDeleteBrickVariableStillVisible() {
		final String variableName = "testVariable";
		onBrickAtPosition(setBrickPosition).onVariableSpinner(R.id.set_variable_spinner)
				.performNewVariable(variableName);

		onBrickAtPosition(setBrickPosition)
				.performDeleteBrick();

		onBrickAtPosition(setBrickPosition).checkShowsText(R.string.brick_change_variable);

		onBrickAtPosition(setBrickPosition).onSpinner(R.id.change_variable_spinner)
				.checkShowsText(variableName);
	}
}
