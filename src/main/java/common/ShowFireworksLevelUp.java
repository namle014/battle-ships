package common;

import javafx.animation.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.Random;

public class ShowFireworksLevelUp {
    private static final Random random = new Random();
    private static Timeline fireworksLoop;

    public static void showFireworks(Pane root) {
        if (fireworksLoop != null && fireworksLoop.getStatus() == Animation.Status.RUNNING) {
            return; // Nếu đang chạy thì không chạy thêm
        }

        fireworksLoop = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> createFirework(root)));
        fireworksLoop.setCycleCount(Animation.INDEFINITE);
        fireworksLoop.play();
    }

    public static void stopFireworks() {
        if (fireworksLoop != null) {
            fireworksLoop.stop();
        }
    }

    private static void createFirework(Pane root) {
        // Giới hạn bắn trong vùng trung tâm (60% kích thước màn hình)
        double centerX = root.getWidth() * 0.2 + random.nextDouble() * root.getWidth() * 0.6;
        double centerY = root.getHeight() * 0.2 + random.nextDouble() * root.getHeight() * 0.6;

        for (int i = 0; i < 50; i++) { // Tăng số lượng tia sáng
            // Gradient màu rực rỡ hơn
            Stop[] stops = new Stop[]{
                    new Stop(0, Color.hsb(random.nextDouble() * 360, 1, 1)), // Màu sáng rực
                    new Stop(0.5, Color.hsb(random.nextDouble() * 360, 1, 0.8)), // Chuyển màu nhẹ
                    new Stop(1, Color.hsb(random.nextDouble() * 360, 1, 0.6)) // Hơi dịu cuối
            };
            LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, null, stops);

            // Tạo tia sáng
            Line ray = new Line(centerX, centerY, centerX, centerY);
            ray.setStroke(gradient);
            ray.setStrokeWidth(random.nextDouble() * 4 + 1);

            // Hiệu ứng tỏa sáng mạnh hơn
            DropShadow glow = new DropShadow();
            glow.setRadius(20); // Tăng độ sáng
            glow.setBlurType(BlurType.GAUSSIAN);
            glow.setColor(stops[0].getColor().deriveColor(0, 1, 2, 1)); // Sáng hơn
            ray.setEffect(glow);

            root.getChildren().add(ray);

            // Xác định góc & khoảng cách
            double angle = random.nextDouble() * 360;
            double distance = 100 + random.nextDouble() * 150; // Xa hơn một chút

            double endX = centerX + distance * Math.cos(Math.toRadians(angle));
            double endY = centerY + distance * Math.sin(Math.toRadians(angle));

            // Hiệu ứng bay & mờ dần nhanh hơn
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1.2), // Bay nhanh hơn
                            new KeyValue(ray.endXProperty(), endX),
                            new KeyValue(ray.endYProperty(), endY),
                            new KeyValue(ray.opacityProperty(), 0)
                    )
            );

            timeline.setOnFinished(e -> root.getChildren().remove(ray));
            timeline.play();
        }
    }
}
