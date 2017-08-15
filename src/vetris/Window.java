package vetris;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Window extends Application {
	private static final int fps = 100;

	private static long time = System.nanoTime();
	private static final long interval = 1_000_000_000 / fps;

	private static final Group root = new Group();
	private static final Key key = new Key();

	private static Stage primaryStage;
	private static final Scene scene = new Scene(root, 22 * Grid.getSquareSize(), 20 * Grid.getSquareSize());

	private static final Background background = new Background();
	private static final Panel panel = new Panel();
	private static final Foreground foreground = new Foreground();

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static Scene getScene() {
		return scene;
	}

	public static void updateColor() {
		background.updateColor();
	}

	private static void updateSize() {
		background.updateSize();

		foreground.updateSize();
	}

	public void repaint() {
		panel.update();
		foreground.update();

		long sleep = time - System.nanoTime() + interval;

		if (sleep > 0) {
			try {
				Thread.sleep(sleep / 1000000, (int) (sleep % 1000000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		time = System.nanoTime();
	}

	@Override
	public synchronized void start(Stage p) {
		primaryStage = p;
		Grid.reset();
		FallBlock.reset();

		scene.setFill(Color.rgb(31, 31, 31));

		root.getChildren().add(key);
		key.requestFocus();
		root.getChildren().add(background);
		root.getChildren().add(panel);
		root.getChildren().add(foreground);

		primaryStage.setTitle("Vetris");
		primaryStage.getIcons().add(new Image(Window.class.getResourceAsStream("/resources/images/Icon.png")));
		primaryStage.setResizable(true);
		primaryStage.setScene(scene);

		primaryStage.setFullScreenExitHint("");
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

		primaryStage.show();

		ChangeListener<Number> resizeListener = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> a, Number b, Number c) {
				int height = (int) (scene.getHeight() / 20);
				int width = (int) (scene.getWidth() / 22);
				int size;

				if (height < width) {
					size = height;
				} else {
					size = width;
				}

				Grid.setSquareSize(size);
				Grid.setTranslate(((int) scene.getWidth() - 22 * size) / 2);

				Window.updateSize();
			}
		};

		scene.widthProperty().addListener(resizeListener);
		scene.heightProperty().addListener(resizeListener);

		new Thread() {
			@Override
			public void run() {
				while (primaryStage.isShowing()) {
					if (!Grid.isPause()) {
						for (int a = 0; a < Grid.getFallingShapes().size(); a++) {
							if (Grid.getFallingShapes().get(a).tick()) {
								Grid.getFallingShapes().remove(a);
							}
						}
						if (Grid.isTinyShape()) {
							FallBlock.tickTinyShape();
						} else {
							FallBlock.tick();
							Grid.addSpeed();
						}
					}

					Window.this.repaint();

					if (Grid.isGameOver()) {
						SoundPlayer.playGameOver();
						foreground.updateGameOver();

						while (primaryStage.isShowing() && Grid.isGameOver()) {
							Window.this.repaint();
						}
					} else if (!primaryStage.isFocused()) {
						Grid.setPause(true);
					}
				}
				if (Grid.getScore() > Grid.getRecord()) {
					Save.saveScore(Grid.getScore());
				}
				ColorManager.saveColor();
			}
		}.start();
	}
}
