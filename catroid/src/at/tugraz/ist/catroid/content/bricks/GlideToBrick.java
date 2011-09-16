/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;
import at.tugraz.ist.catroid.ui.dialogs.EditIntegerDialog;

public class GlideToBrick implements Brick, OnDismissListener, OnClickListener {
	private static final long serialVersionUID = 1L;
	private int xDestination;
	private int yDestination;
	private int durationInMilliSeconds;
	private Sprite sprite;

	private transient View view;

	public GlideToBrick(Sprite sprite, int xDestination, int yDestination, int durationInMilliSeconds) {
		this.sprite = sprite;
		this.xDestination = xDestination;
		this.yDestination = yDestination;
		this.durationInMilliSeconds = durationInMilliSeconds;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		int duration = durationInMilliSeconds;
		while (duration > 0) {
			long timeBeforeSleep = System.currentTimeMillis();
			int sleep = 33;
			while (System.currentTimeMillis() <= (timeBeforeSleep + sleep)) {
				if (sprite.isPaused) {
					sleep = (int) ((timeBeforeSleep + sleep) - System.currentTimeMillis());
					long milliSecondsBeforePause = System.currentTimeMillis();
					while (sprite.isPaused) {
						if (sprite.isFinished) {
							return;
						}
						Thread.yield();
					}
					timeBeforeSleep = System.currentTimeMillis();
					startTime += System.currentTimeMillis() - milliSecondsBeforePause;
				}

				Thread.yield();
			}
			long currentTime = System.currentTimeMillis();
			duration -= (int) (currentTime - startTime);
			updatePositions((int) (currentTime - startTime), duration);
			startTime = currentTime;
			sprite.setToDraw(true);
		}
		sprite.setXYPosition(xDestination, yDestination);
		sprite.setToDraw(true);
	}

	private void updatePositions(int timePassed, int duration) {
		int xPosition = sprite.getXPosition();
		int yPosition = sprite.getYPosition();

		xPosition += ((float) timePassed / duration) * (xDestination - xPosition);
		yPosition += ((float) timePassed / duration) * (yDestination - yPosition);

		sprite.setXYPosition(xPosition, yPosition);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public int getDurationInMilliSeconds() {
		return durationInMilliSeconds;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_glide_to, null);
		}

		EditText editX = (EditText) view.findViewById(R.id.toolbox_brick_glide_to_x_edit_text);
		editX.setText(String.valueOf(xDestination));
		//		EditIntegerDialog dialogX = new EditIntegerDialog(context, editX, xDestination, true);
		//		dialogX.setOnDismissListener(this);
		//		dialogX.setOnCancelListener((OnCancelListener) context);
		//		editX.setOnClickListener(dialogX);
		editX.setOnClickListener(this);

		EditText editY = (EditText) view.findViewById(R.id.toolbox_brick_glide_to_y_edit_text);
		editY.setText(String.valueOf(yDestination));
		//		EditIntegerDialog dialogY = new EditIntegerDialog(context, editY, yDestination, true);
		//		dialogY.setOnDismissListener(this);
		//		dialogY.setOnCancelListener((OnCancelListener) context);
		//		editY.setOnClickListener(dialogY);
		editY.setOnClickListener(this);

		EditText editDuration = (EditText) view.findViewById(R.id.toolbox_brick_glide_to_duration_edit_text);
		editDuration.setText(String.valueOf(durationInMilliSeconds / 1000.0));
		//		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, durationInMilliSeconds / 1000.0);
		//		dialogDuration.setOnDismissListener(this);
		//		dialogDuration.setOnCancelListener((OnCancelListener) context);
		//		editDuration.setOnClickListener(dialogDuration);
		editDuration.setOnClickListener(this);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_glide_to, null);
	}

	@Override
	public Brick clone() {
		return new GlideToBrick(getSprite(), xDestination, yDestination, getDurationInMilliSeconds());
	}

	public void onDismiss(DialogInterface dialog) {
		if (dialog instanceof EditIntegerDialog) {
			EditIntegerDialog inputDialog = (EditIntegerDialog) dialog;
			if (inputDialog.getRefernecedEditTextId() == R.id.toolbox_brick_glide_to_x_edit_text) {
				xDestination = inputDialog.getValue();
			} else if (inputDialog.getRefernecedEditTextId() == R.id.toolbox_brick_glide_to_y_edit_text) {
				yDestination = inputDialog.getValue();
			} else {
				throw new RuntimeException("Received illegal id from EditText: "
						+ inputDialog.getRefernecedEditTextId());
			}
		} else if (dialog instanceof EditDoubleDialog) {
			durationInMilliSeconds = (int) Math.round(((EditDoubleDialog) dialog).getValue() * 1000);
		}
		dialog.cancel();
	}

	public void onClick(final View view) {
		final Context context = view.getContext();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		if (view.getId() == R.id.toolbox_brick_glide_to_x_edit_text) {
			input.setText(String.valueOf(xDestination));
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
		} else if (view.getId() == R.id.toolbox_brick_glide_to_y_edit_text) {
			input.setText(String.valueOf(yDestination));
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
		} else if (view.getId() == R.id.toolbox_brick_glide_to_duration_edit_text) {
			input.setText(String.valueOf(durationInMilliSeconds / 1000.0));
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
					| InputType.TYPE_NUMBER_FLAG_SIGNED);
		}
		input.setSelectAllOnFocus(true);
		dialog.setView(input);
		dialog.setOnCancelListener((OnCancelListener) context);
		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				try {
					if (view.getId() == R.id.toolbox_brick_glide_to_x_edit_text) {
						xDestination = Integer.parseInt(input.getText().toString());
					} else if (view.getId() == R.id.toolbox_brick_glide_to_y_edit_text) {
						yDestination = Integer.parseInt(input.getText().toString());
					} else if (view.getId() == R.id.toolbox_brick_glide_to_duration_edit_text) {
						durationInMilliSeconds = (int) Math.round(Double.parseDouble(input.getText().toString()) * 1000);
					}
				} catch (NumberFormatException exception) {
					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT);
				}
				dialog.cancel();
			}
		});
		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		dialog.show();

	}
}
