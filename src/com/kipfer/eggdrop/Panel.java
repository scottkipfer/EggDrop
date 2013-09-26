package com.kipfer.eggdrop;

import java.text.DecimalFormat;
import com.google.ads.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;



class Panel extends SurfaceView implements SurfaceHolder.Callback {

	private boolean backgroundwidth = true;
	private EggDropThread _thread;
	private ArrayList<GraphicObject> _graphics = new ArrayList<GraphicObject>();
	private ArrayList<GraphicObject> eggs = new ArrayList<GraphicObject>();
	private GraphicObject _currentGraphic = null;
	private ArrayList<GraphicObject> _explosions = new ArrayList<GraphicObject>();
	private SoundPool _soundPool;
	private int _playbackFile3 = 0;
	private int _playbackFile4 = 0;
	private int _playbackFile5 = 0;
	private boolean succeed;
	private Integer onlineposition;
	private int numofusers =0;


	Random random = new Random();
	private int rand = Math.abs(random.nextInt() % 600);
	private int stopvalue = (rand + 1);
	private GraphicObject hen;
	private GraphicObject basket;
	private GraphicObject basket2;
	private GraphicObject shadow;
	private int _x = 100;
	private int _y = 400;
	private int _y3;
	private int _y2;
	Bitmap background;
	Bitmap bg;
	Bitmap ground;
	Bitmap board;
	Bitmap br;
	Bitmap gr;
	Bitmap fb;
	Bitmap failback;
	Paint text;
	Paint text2;
	Paint text3;
	Paint text4;
	private Highscore scorecalc;
	DecimalFormat f = new DecimalFormat("##.00");
	String Id = "";

	// Game Play Variables
	// --------------------------------------------------------------------

	private int basketnum = 2;
	private boolean pause = true;
	private boolean startmessage = true;
	private boolean fail = false;
	private int caught = 0;
	private int count = 0;
	public int score = 0;
	private int eggspeed = 5;
	private int droprate = 10;
	public int total = 0;
	public int load = 0;
	private int level = 1;
	private int endingEgg = 100;
	private int point = 0;
	private int henspeed = 20;
	private int made = 0;
	private boolean newlevel = true;
	private int reward = 1000;
	private int highscore;
	private boolean newhighscore;
	private int splatspot;
	private boolean brokenegg;
	private int gamesplayed;
	private double average;
	DbHelper dbHelper;
	SQLiteDatabase db;
	ContentValues cv;
	AdRequest adRequest;
	private boolean wifi;
	private boolean mobile;

	// ---------------------------------------------------------------------

	public Panel(Context context) {
		super(context);
		
		
		_soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 100);
		_playbackFile3 = _soundPool.load(getContext(), R.raw.pop, 0);
		_playbackFile4 = _soundPool.load(getContext(), R.raw.reward, 1);
		_playbackFile5 = _soundPool.load(getContext(), R.raw.splat, 1);
		getHolder().addCallback(this);
		_thread = new EggDropThread(getHolder(), this);
		setFocusable(true);
		hen = new GraphicObject(BitmapFactory.decodeResource(getResources(),
				R.drawable.chickenright));
		hen.getCoordinates().setX(getWidth() / 2);
		hen.getCoordinates().setY(50);
		_graphics.add(hen);

		text = new Paint();
		text.setColor(Color.WHITE);
		text.setTextSize(50);
		text.setAntiAlias(true);
		text.setTextAlign(Align.RIGHT);
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"planetbe.ttf");
		text.setTypeface(tf);

		text4 = new Paint();
		text4.setColor(Color.RED);
		text4.setTextSize(50);
		text4.setAntiAlias(true);
		text4.setTextAlign(Align.LEFT);
		Typeface tz = Typeface.createFromAsset(getContext().getAssets(),
				"planetbe.ttf");
		text4.setTypeface(tz);

		text3 = new Paint();
		text3.setColor(Color.BLACK);
		text3.setTextSize(50);
		text3.setAntiAlias(true);
		text3.setTextAlign(Align.CENTER);
		Typeface tx = Typeface.createFromAsset(getContext().getAssets(),
				"earthquakemf.ttf");
		text3.setTypeface(tx);

		text2 = new Paint();
		text2.setColor(Color.BLACK);
		text2.setTextSize(30);
		text2.setTextAlign(Align.CENTER);
		text2.setAntiAlias(true);
		
		
		
		// See if there is an Internet connection
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                wifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                mobile = true;
		}

		
		// need to change this to  ASYNC TASK
		dbHelper = new DbHelper(getContext());
		scorecalc = new Highscore(dbHelper);
		gamesplayed = scorecalc.getNumGames();
		if (gamesplayed > 0) {
			highscore = scorecalc.getLocalHighScore();
			average = scorecalc.getAverage();
			Id = scorecalc.getId();
			if (Id.equals("null")) {
				if(wifi || mobile){
				Id = scorecalc.createId();
				db = dbHelper.getWritableDatabase();
				cv = new ContentValues();
				cv.put("username", Id);
				cv.put("score", highscore);
				Calendar c = Calendar.getInstance();
				int date = c.get(Calendar.DATE);
				cv.put("date", date);
				db.update("scores", cv, "score=" + highscore, null);
				db.close();}
			}
		} else {
			highscore = 0;
			average = 0;
			if(wifi || mobile){
			Id = scorecalc.createId();
			}
		}
		dbHelper.close();

	}

	
	
	// This is where you get the touch input to control either the basket or to advance to 
	// The next screen
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (_thread.getSurfaceHolder()) {
			_x = (int) event.getX();

			if (pause && !fail) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					pause = false;
					brokenegg = false;
				}
			}

			if (fail) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					startmessage = true;
					fail = false;
				}
			}
			return true;
		}
	}

	
	//  This checks for hit ... also updates database... maybe not the best place to do this...
	// looking for alternatives
	public void checkForHit() {
		ArrayList<GraphicObject> toExplosion = new ArrayList<GraphicObject>();
		for (GraphicObject egg : eggs) {
			if (checkCollision(egg) == 1) {
				splatspot = egg.getCoordinates().getX();
				brokenegg = true;
				for (GraphicObject eg : eggs) {
					toExplosion.add(eg);
				}
				_soundPool.play(_playbackFile5, 1, 1, 0, 0, 1);
				pause = true;
				if (basketnum == 2) {

					if (level > 1) {
						level = level - 1;
						if (level > 5) {
							droprate = droprate + 2;
							eggspeed = eggspeed - 1;
							henspeed = henspeed - 1;
							point = point - 1;
							endingEgg = endingEgg - 10;
						} else {
							newlevel = true;
						}
					}
					made = 0;
					caught = 0;
					count = 0;
					basketnum = 1;
					continue;
				}

				if (basketnum == 1) {

					level = 0;
					made = 0;
					newlevel = true;

					// ----HIGH SCORE STUFF -------------
					gamesplayed = scorecalc.getNumGames();
					if (gamesplayed > 0) {
						highscore = scorecalc.getLocalHighScore();

					} else {
						highscore = 0;
					}

					db = dbHelper.getWritableDatabase();
					cv = new ContentValues();
					cv.put("username", Id);
					cv.put("score", score);
					Calendar c = Calendar.getInstance();
					int date = c.get(Calendar.DATE);
					cv.put("date", date);
					db.insert("scores", null, cv);
					db.close();

					gamesplayed = scorecalc.getNumGames();
					average = scorecalc.getAverage();
					
					
					if (score > highscore) {
						newhighscore = true;
						highscore = score;
						
						
						// maybe check wifi/mobile status here also
						//change to async task
						if(wifi || mobile){
						succeed = scorecalc.upDateOnlineHighScore(Id,
								highscore, average, gamesplayed);
						
						
						// change to async task
						numofusers = scorecalc.getPostion(highscore);
						if(!succeed){
							Toast.makeText(getContext(), "There is no Internet connection.  Please make sure you have Data or WiFi enabled.", Toast.LENGTH_LONG).show();
													}
						 onlineAccess access = new onlineAccess();
						  access.execute(""+highscore);
						
						try {
							onlineposition = access.get();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						}
						else{succeed = false;}
						fail = true;

					} else {
						// check for wifi/mobile
						// change to async task
						if(wifi || mobile){
						// change to asyc task
						succeed = scorecalc.upDateOnlineHighScore(Id,
								highscore, average, gamesplayed);
						numofusers = scorecalc.getPostion(highscore);
						if(!succeed){
							Toast.makeText(getContext(), "There is no Internet connection.  Please make sure you have Data or WiFi enabled.", Toast.LENGTH_LONG).show();
													}
						  onlineAccess access = new onlineAccess();
						  access.execute(""+highscore);
						try {
							onlineposition = access.get();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						}else{succeed = false;}
						fail = true;
					}

				}
			} else if (checkCollision(egg) == 2) {
				toExplosion.add(egg);
				score = score + point;
				caught = caught + 1;
				if (score > reward) {
					_soundPool.play(_playbackFile4, 1, 1, 0, 0, 1);
					if (basketnum == 1) {
						basketnum = 2;
					}
					reward = reward * 2;
				}
				_soundPool.play(_playbackFile3, 1, 1, 0, 0, 1);
				if (caught == endingEgg) {
					pause = true;
					caught = 0;
					count = 0;
					made = 0;
					level = level + 1;
					newlevel = true;
				}
			} else {
				continue;
			}
		}
		if (!toExplosion.isEmpty()) {
			_explosions.addAll(toExplosion);
			eggs.removeAll(toExplosion);
			_explosions.removeAll(toExplosion);
		}

	}
public boolean getFail(){
	return fail;
	
}
	private int checkCollision(GraphicObject first) {
		int retValue = 0;
		int width = first.getGraphic().getWidth();
		int height = first.getGraphic().getHeight();
		int basketwidth = basket.getGraphic().getWidth();
		int basketheight = basket.getGraphic().getHeight();
		int firstYRangeStart = first.getCoordinates().getY();
		int firstYRangeEnd = first.getCoordinates().getY() + height;
		int firstXRangeStart = first.getCoordinates().getX();
		int firstXRangeEnd = first.getCoordinates().getX() + width;
		int basketYRangeStart = _y;
		int basketYRangeEnd = _y + basketheight;
		int basketXRangeStart = _x - basketwidth / 2 - 10;
		int basketXRangeEnd = _x + basketwidth / 2 + 10;
		int basket2YRangeStart = _y2;
		int basket2YRangeEnd = _y2 + basketheight;

		if ((firstYRangeStart >= getHeight())
				|| (firstYRangeEnd >= getHeight() - 20)) {
			retValue = 1;
		}
		if (basketnum > 1) {
			if ((basketXRangeStart >= firstXRangeStart && basketXRangeStart <= firstXRangeEnd)
					|| (basketXRangeEnd >= firstXRangeStart && basketXRangeEnd <= firstXRangeEnd)
					|| (firstXRangeStart >= basketXRangeStart && firstXRangeEnd <= basketXRangeEnd)) {
				if ((basketYRangeStart >= firstYRangeStart && basketYRangeStart <= firstYRangeEnd)
						|| (basketYRangeEnd >= firstYRangeStart && basketYRangeEnd <= firstYRangeEnd)) {
					retValue = 2;
				}
			}
		}

		if ((basketXRangeStart >= firstXRangeStart && basketXRangeStart <= firstXRangeEnd)
				|| (basketXRangeEnd >= firstXRangeStart && basketXRangeEnd <= firstXRangeEnd)
				|| (firstXRangeStart >= basketXRangeStart && firstXRangeEnd <= basketXRangeEnd)) {
			if ((basket2YRangeStart >= firstYRangeStart && basket2YRangeStart <= firstYRangeEnd)
					|| (basket2YRangeEnd >= firstYRangeStart && basket2YRangeEnd <= firstYRangeEnd)) {
				retValue = 2;
			}
		}
		return retValue;
	}

	public void setStuff() {
		if (!pause) {
			if (newlevel) {
				switch (level) {
				case 0:
					newhighscore = false;
					reward = 1000;
					count = 0;
					total = 0;
					caught = 0;
					fail = false;
					score = 0;
					load = 50;
					level = 1;
					basketnum = 2;

					break;
				case 1:
					startmessage = false;
					droprate = 25;
					eggspeed = 2;
					endingEgg = 10;
					point = 1;
					henspeed = 5;
					newlevel = false;

					break;
				case 2:
					droprate = 23;
					eggspeed = 4;
					endingEgg = 20;
					point = 2;
					newlevel = false;

					break;
				case 3:
					droprate = 20;
					eggspeed = 6;
					point = 3;
					endingEgg = 30;
					henspeed = 7;
					newlevel = false;

					break;
				case 4:
					droprate = 18;
					eggspeed = 7;
					point = 4;
					endingEgg = 40;
					newlevel = false;
					break;
				case 5:
					droprate = droprate - 2;
					eggspeed = eggspeed + 1;
					henspeed = henspeed + 1;
					point = point + 1;
					endingEgg = endingEgg + 10;
					newlevel = false;
					break;
				case 6:
					droprate = droprate - 2;
					eggspeed = eggspeed + 1;
					henspeed = henspeed + 1;
					point = point + 1;
					endingEgg = endingEgg + 10;
					newlevel = false;
					break;
				case 7:
					droprate = droprate - 2;
					eggspeed = eggspeed + 1;
					henspeed = henspeed + 1;
					point = point + 1;
					endingEgg = endingEgg + 10;
					newlevel = false;
					break;
				case 8:
					droprate = droprate - 2;
					eggspeed = eggspeed + 1;
					henspeed = henspeed + 1;
					point = point + 1;
					endingEgg = endingEgg + 10;
					newlevel = false;
					break;
				case 9:
					droprate = droprate - 2;
					eggspeed = eggspeed + 1;
					henspeed = henspeed + 1;
					point = point + 1;
					endingEgg = endingEgg + 10;
					newlevel = false;
					break;
				case 10:
					droprate = droprate - 2;
					eggspeed = eggspeed + 1;
					henspeed = henspeed + 1;
					point = point + 1;
					endingEgg = endingEgg + 10;
					newlevel = false;
					break;
				default:
					if (droprate > 2) {
						droprate = droprate - 2;
					}
					eggspeed = eggspeed + 1;
					henspeed = henspeed + 1;
					point = point + 1;
					endingEgg = endingEgg + 10;
					newlevel = false;
					break;
				}
			}

		}
	}

	public void makeEgg() {
		if (!pause) {
			if (load > 100) {
				if (count == droprate && made < endingEgg) {
					GraphicObject egg = new GraphicObject(
							BitmapFactory.decodeResource(getResources(),
									R.drawable.egg));
					egg.getCoordinates().setX(hen.getCoordinates().getX());
					egg.getCoordinates().setY(
							hen.getCoordinates().getY()
									+ hen.getGraphic().getHeight());
					eggs.add(egg);
					count = 0;
					total = total + 1;
					made = made + 1;

				} else {
					count = count + 1;
				}

			}
			load = load + 1;
		}
	}

	public void updatePhysics() {
		if (!pause) {
			GraphicObject.Coordinates coord;
			GraphicObject.Speed speed;

			for (GraphicObject egg : eggs) {
				coord = egg.getCoordinates();
				coord.setY(coord.getY() + eggspeed);
			}

			for (GraphicObject graphic : _graphics) {
				coord = graphic.getCoordinates();
				speed = graphic.getSpeed();

				if (speed.getXDirection() == GraphicObject.Speed.X_DIRECTION_RIGHT) {
					coord.setX(coord.getX() + henspeed);
				} else {
					coord.setX(coord.getX() - henspeed);
				}

				if (speed.getYDirection() == GraphicObject.Speed.Y_DIRECTION_DOWN) {
					coord.setY(coord.getY() + speed.getY());
				} else {
					coord.setY(coord.getY() - speed.getY());
				}

				// borders for x

				if (coord.getX() < 0) {
					speed.toggleXDirection();
					graphic.changeGraphic(BitmapFactory.decodeResource(
							getResources(), R.drawable.chickenright));
					coord.setX(-coord.getX());
				} else if (coord.getX() + graphic.getGraphic().getWidth() > getWidth()) {
					speed.toggleXDirection();
					graphic.changeGraphic(BitmapFactory.decodeResource(
							getResources(), R.drawable.chicken50));
					coord.setX(coord.getX() + getWidth()
							- (coord.getX() + graphic.getGraphic().getWidth()));
				} else if (speed.getXDirection() < 0
						&& coord.getX() < stopvalue) {
					speed.toggleXDirection();
					graphic.changeGraphic(BitmapFactory.decodeResource(
							getResources(), R.drawable.chickenright));
					rand = Math.abs(random.nextInt() % getWidth());
					stopvalue = (rand + 1);
				} else if (speed.getXDirection() > 0
						&& coord.getX() > stopvalue) {
					speed.toggleXDirection();
					graphic.changeGraphic(BitmapFactory.decodeResource(
							getResources(), R.drawable.chicken50));
					rand = Math.abs(random.nextInt() % getWidth());
					stopvalue = (rand + 1);
					if (stopvalue >= getWidth()) {
						stopvalue = stopvalue - 7;
					}
				}

			}
		}

	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GRAY);
		setImages();
		
		if (!backgroundwidth) {
			canvas.drawBitmap(bg, 0, 0, null);
			// canvas.drawBitmap(gr, 0, getHeight() - gr.getHeight(), null);
			canvas.drawBitmap(br, 0, 100, null);
		}
		if (brokenegg) {
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
					R.drawable.brokentinyegg), splatspot, (getHeight() - Math.round(getHeight() * (float).10417)),
					null);
		}
		Bitmap bitmap;
		GraphicObject.Coordinates coords;

		canvas.drawText("" + score, getWidth() - 10, 50, text);
		if (highscore > score) {
			canvas.drawText("" + highscore, 10, 50, text4);
		} else {
			canvas.drawText("" + score, 10, 50, text4);
		}
		
		

		if (basketnum == 2) {
			canvas.drawBitmap(basket.getGraphic(), _x
					- (basket.getGraphic().getWidth() / 2), _y, null);
			canvas.drawBitmap(basket.getGraphic(), _x
					- (basket.getGraphic().getWidth() / 2), _y2, null);
			canvas.drawBitmap(shadow.getGraphic(), _x
					- (shadow.getGraphic().getWidth() / 2), _y3, null);
		}

		if (basketnum == 1) {
			canvas.drawBitmap(basket.getGraphic(), _x
					- (basket2.getGraphic().getWidth() / 2), _y2, null);
			canvas.drawBitmap(shadow.getGraphic(), _x
					- (shadow.getGraphic().getWidth() / 2), _y3, null);
		}

		for (GraphicObject graphic : eggs) {
			bitmap = graphic.getGraphic();
			coords = graphic.getCoordinates();
			canvas.drawBitmap(bitmap, coords.getX(), coords.getY(), null);
		}

		for (GraphicObject graphic : _graphics) {
			bitmap = graphic.getGraphic();
			coords = graphic.getCoordinates();
			canvas.drawBitmap(bitmap, coords.getX(), coords.getY(), null);
		}

		if (_currentGraphic != null) {
			bitmap = _currentGraphic.getGraphic();
			coords = _currentGraphic.getCoordinates();
			canvas.drawBitmap(bitmap, coords.getX(), coords.getY(), null);
		}

		if (pause && !fail && !startmessage) {
			canvas.drawText("Touch the Screen to Continue", getWidth() / 2,
					getHeight() / 2, text2);

		}
		if (startmessage) {
			canvas.drawText("Touch the Screen to Start", getWidth() / 2,
					getHeight() / 2, text2);

		}
		
		if (fail) {
			if (newhighscore) {
				canvas.drawBitmap(fb, Math.round(getWidth()*(float)(.15875)),Math.round(getHeight()*(float)(.20833/2)),null);
				canvas.drawText("New High Score", getWidth() / 2,
						getHeight() / 2-100, text3);
				if(succeed){
				canvas.drawText("Online Rank: " + onlineposition+ " Out of " + numofusers,
						getWidth() / 2, (getHeight() / 2) , text2);

				canvas.drawText("Average: " + f.format(average),
						getWidth() / 2, (getHeight() / 2) + 50, text2);
				canvas.drawText("Games Played: " + gamesplayed,
						getWidth() / 2, (getHeight() / 2) + 100, text2);}
				else{
	
					canvas.drawText("Failed to get Online Rank",
							getWidth() / 2, (getHeight() / 2) , text2);
					canvas.drawText("Check Network Connections",
							getWidth() / 2, (getHeight() / 2+50) , text2);
				
				canvas.drawText("Average: " + f.format(average),
						getWidth() / 2, (getHeight() / 2) + 100, text2);
				canvas.drawText("Games Played: " + gamesplayed,
						getWidth() / 2, (getHeight() / 2) + 150, text2);}
			} else {
				canvas.drawBitmap(bg, 0, 0, null);
				canvas.drawBitmap(br, 0, 100, null);
				canvas.drawBitmap(fb, Math.round(getWidth()*(float)(.15875)),Math.round(getHeight()*(float)(.20833/2)),null);
				canvas.drawText("" + score, getWidth() - 10, 50, text);
				
				if (highscore > score) {
					canvas.drawText("" + highscore, 10, 50, text4);
				} else {
					canvas.drawText("" + score, 10, 50, text4);
				}
				canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.brokentinyegg), splatspot,
						(getHeight() - 50), null);
				canvas.drawText("Game Over", getWidth() / 2, getHeight() / 2-100,
						text3);
				if(succeed){
					canvas.drawText("Online Rank: " + onlineposition + " Out of " + numofusers,
							getWidth() / 2, (getHeight() / 2) , text2);
					canvas.drawText("Average: " + f.format(average),
							getWidth() / 2, (getHeight() / 2) + 50, text2);
					canvas.drawText("Games Played: " + gamesplayed,
							getWidth() / 2, (getHeight() / 2) + 100, text2);}
					else{
						canvas.drawText("Failed to get Online Rank",
								getWidth() / 2, (getHeight() / 2) , text2);
				canvas.drawText("Check Network Connections",
						getWidth() / 2, (getHeight() / 2+50) , text2);
				
				canvas.drawText("Average: " + f.format(average),
						getWidth() / 2, (getHeight() / 2) + 100, text2);
				canvas.drawText("Games Played: " + gamesplayed,
						getWidth() / 2, (getHeight() / 2) + 150, text2);
				
			}
				
			}
		}
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		_thread.setRunning(true);
		if (!_thread.isAlive()) {
			_thread = new EggDropThread(holder, this);
		}
		_thread.setRunning(true);
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		_thread.setRunning(false);
		while (retry) {
				_thread.interrupt();
			retry = false;
		}
	}
	
	public void setImages(){
		if (backgroundwidth) {
			
			basket = new GraphicObject(BitmapFactory.decodeResource(getResources(),R.drawable.basketcatch));
			basket2 = new GraphicObject(BitmapFactory.decodeResource(getResources(), R.drawable.basketcatch));
			shadow = new GraphicObject(BitmapFactory.decodeResource(getResources(),R.drawable.basketshadow));
			ground = BitmapFactory.decodeResource(getResources(),R.drawable.ground);
			background = BitmapFactory.decodeResource(getResources(),R.drawable.winterbackground2);
			board = BitmapFactory.decodeResource(getResources(),R.drawable.snowwood);
			failback = BitmapFactory.decodeResource(getResources(),R.drawable.blueseethru);
			
			//Set Height of baskets
			_y = getHeight() - Math.round(getHeight() * (float) .15625);
			_y2 = getHeight() - Math.round(getHeight() * (float) .26042);
			_y3 = getHeight() - Math.round(getHeight() * (float) .0625);
			
			//set Height of Hen
			hen.getCoordinates().setY(Math.round(getHeight() * (float) .10416));
			
			
			// Edit the sizes of the Images so they fit the screen better
			
	
			Matrix matrix = new Matrix();
			Matrix matrix2 = new Matrix();
			Matrix matrix3 = new Matrix();
			Matrix matrix4 = new Matrix();
			matrix.postScale(((float) getWidth()) / background.getWidth(),((float) getHeight()) / background.getHeight());
			bg = Bitmap.createBitmap(background, 0, 0, background.getWidth(),background.getHeight(), matrix, true);
			matrix2.postScale(((float) getWidth()) / ground.getWidth(),((float) 50) / ground.getHeight());
			gr = Bitmap.createBitmap(ground, 0, 0, ground.getWidth(),ground.getHeight(), matrix2, true);
			matrix3.postScale(((float) getWidth()) / board.getWidth(),((float) 30) / board.getHeight());
			br = Bitmap.createBitmap(board, 0, 0, board.getWidth(),board.getHeight(), matrix3, true);
			matrix4.postScale(((float) getWidth()-Math.round(getWidth()*(float).31667)) / failback.getWidth(),((float) getHeight()-Math.round(getHeight()*(float).20833)) / failback.getHeight());
			fb = Bitmap.createBitmap(failback, 0, 0, failback.getWidth(),failback.getHeight(), matrix4, true);
			
			backgroundwidth = false;

			
		}
	}

}
