import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Enhanced Rogue-like Dungeon Crawler with modern graphics and smooth animations
 */
public class DungeonCrawler extends JFrame {

    private GamePanel gamePanel;

    public DungeonCrawler() {
        setTitle("Enhanced Dungeon Crawler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel();
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            // Corrected method name to get the system's look and feel class name as a string
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(DungeonCrawler::new);
    }

    class GamePanel extends JPanel {
        // Game Constants
        private static final int TILE_SIZE = 32;
        private static final int MAP_WIDTH = 35;
        private static final int MAP_HEIGHT = 22;
        private static final int PANEL_WIDTH = MAP_WIDTH * TILE_SIZE;
        private static final int PANEL_HEIGHT = MAP_HEIGHT * TILE_SIZE + 120; // Extra space for UI

        // Game State
        private Player player;
        private List<Monster> monsters;
        private List<Item> items;
        private Tile[][] map;
        private Point stairs;
        private int dungeonLevel = 1;
        private String message = "Welcome, brave adventurer! Find the glowing portal to descend deeper.";
        private Timer animationTimer;
        private long gameTime = 0;

        // Visual enhancements
        private BufferedImage shadowLayer;
        private Random visualRandom = new Random();
        
        // Enhanced color scheme with gradients
        private final Color[] FLOOR_COLORS = {
            new Color(45, 45, 55),
            new Color(40, 42, 50),
            new Color(50, 48, 60)
        };
        private final Color[] WALL_COLORS = {
            new Color(25, 25, 30),
            new Color(20, 20, 25),
            new Color(30, 30, 35)
        };
        private final Color SHADOW_COLOR = new Color(0, 0, 0, 60);
        
        // Entity colors with glow effects
        private final Color PLAYER_COLOR = new Color(100, 200, 255);
        private final Color PLAYER_GLOW = new Color(100, 200, 255, 80);
        private final Color MONSTER_COLOR = new Color(220, 60, 60);
        private final Color MONSTER_GLOW = new Color(220, 60, 60, 100);
        private final Color ITEM_COLOR = new Color(255, 215, 0);
        private final Color ITEM_GLOW = new Color(255, 215, 0, 120);
        private final Color STAIRS_COLOR = new Color(180, 100, 255);
        private final Color STAIRS_GLOW = new Color(180, 100, 255, 150);
        
        // UI colors
        private final Color UI_BG = new Color(20, 20, 25, 200);
        private final Color UI_BORDER = new Color(100, 100, 120);
        private final Color HEALTH_HIGH = new Color(100, 220, 100);
        private final Color HEALTH_MED = new Color(255, 165, 0);
        private final Color HEALTH_LOW = new Color(255, 80, 80);

        // Fonts
        private Font gameFont;
        private Font uiFont;
        private Font titleFont;

        public GamePanel() {
            setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            setBackground(new Color(15, 15, 20));
            setFocusable(true);
            
            // Initialize fonts
            try {
                gameFont = new Font("Segoe UI Symbol", Font.BOLD, 24);
                uiFont = new Font("Segoe UI", Font.PLAIN, 14);
                titleFont = new Font("Segoe UI", Font.BOLD, 16);
            } catch (Exception e) {
                gameFont = new Font("Monospaced", Font.BOLD, 24);
                uiFont = new Font("SansSerif", Font.PLAIN, 14);
                titleFont = new Font("SansSerif", Font.BOLD, 16);
            }

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    handleKeyPress(e.getKeyCode());
                }
            });

            // Animation timer for smooth effects
            animationTimer = new Timer(50, e -> {
                gameTime += 50;
                repaint();
            });
            animationTimer.start();

            newGame();
        }

        private void newGame() {
            dungeonLevel = 1;
            message = "Welcome, brave adventurer! Find the glowing portal to descend deeper.";
            generateLevel();
            createShadowLayer();
        }

        private void nextLevel() {
            dungeonLevel++;
            message = "You descend deeper into the mystical depths... (Level " + dungeonLevel + ")";
            generateLevel();
            createShadowLayer();
        }

        private void createShadowLayer() {
            shadowLayer = new BufferedImage(PANEL_WIDTH, MAP_HEIGHT * TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = shadowLayer.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            for (int x = 0; x < MAP_WIDTH; x++) {
                for (int y = 0; y < MAP_HEIGHT; y++) {
                    if (map[x][y] == Tile.WALL) {
                        // Add soft shadows around walls
                        g.setColor(SHADOW_COLOR);
                        g.fillRoundRect(x * TILE_SIZE + 2, y * TILE_SIZE + 2, 
                                      TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);
                    }
                }
            }
            g.dispose();
        }

        private void generateLevel() {
            MapGenerator generator = new MapGenerator(MAP_WIDTH, MAP_HEIGHT);
            map = generator.generateMap();
            monsters = new ArrayList<>();
            items = new ArrayList<>();

            Point startPos = generator.getRandomRoomCenter();
            player = new Player(startPos.x, startPos.y);

            stairs = generator.getRandomRoomCenter();
            while (stairs.equals(startPos)) {
                stairs = generator.getRandomRoomCenter();
            }

            populateDungeon(generator.getRooms());
        }

        private void populateDungeon(List<Rectangle> rooms) {
            Random rand = new Random();
            for (Rectangle room : rooms) {
                if (rand.nextInt(100) < 40 + dungeonLevel * 8) {
                    Point pos = getRandomPointInRoom(room);
                    if (isPositionFree(pos.x, pos.y)) {
                        monsters.add(new Monster(pos.x, pos.y, 8 + dungeonLevel * 4, 3 + dungeonLevel));
                    }
                }
                if (rand.nextInt(100) < 15) {
                    Point pos = getRandomPointInRoom(room);
                    if (isPositionFree(pos.x, pos.y)) {
                        items.add(new Item(pos.x, pos.y, "Health Elixir", '♥'));
                    }
                }
                if (rand.nextInt(100) < 5) {
                    Point pos = getRandomPointInRoom(room);
                    if (isPositionFree(pos.x, pos.y)) {
                        items.add(new Item(pos.x, pos.y, "Power Crystal", '◆'));
                    }
                }
            }
        }

        private Point getRandomPointInRoom(Rectangle room) {
            Random rand = new Random();
            int x = rand.nextInt(Math.max(1, room.width - 2)) + room.x + 1;
            int y = rand.nextInt(Math.max(1, room.height - 2)) + room.y + 1;
            return new Point(x, y);
        }

        private boolean isPositionFree(int x, int y) {
            if (player.x == x && player.y == y) return false;
            if (stairs.x == x && stairs.y == y) return false;
            for (Monster m : monsters) {
                if (m.x == x && m.y == y) return false;
            }
            for (Item i : items) {
                if (i.x == x && i.y == y) return false;
            }
            return true;
        }

        private void handleKeyPress(int keyCode) {
            int dx = 0, dy = 0;
            switch (keyCode) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:    dy = -1; break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:    dy = 1;  break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:    dx = -1; break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:    dx = 1;  break;
                case KeyEvent.VK_SPACE:
                case KeyEvent.VK_H:
                    if (player.hp > 0) player.usePotion();
                    repaint();
                    return;
                case KeyEvent.VK_R:
                    if (player.hp <= 0) newGame();
                    repaint();
                    return;
                default: return;
            }

            if (player.hp > 0) {
                movePlayer(dx, dy);
                if (player.hp > 0) {
                    updateMonsters();
                }
            }
            repaint();
        }

        private void movePlayer(int dx, int dy) {
            int nextX = player.x + dx;
            int nextY = player.y + dy;

            if (nextX < 0 || nextX >= MAP_WIDTH || nextY < 0 || nextY >= MAP_HEIGHT) {
                return;
            }

            if (map[nextX][nextY] == Tile.WALL) {
                message = "The ancient stone blocks your path...";
                return;
            }

            for (Monster monster : new ArrayList<>(monsters)) {
                if (monster.x == nextX && monster.y == nextY) {
                    player.attack(monster);
                    if (monster.hp <= 0) {
                        message = "⚔ Victory! The creature falls before your might!";
                        monsters.remove(monster);
                        player.gainExperience(10);
                    } else {
                        monster.attack(player);
                        message = "⚔ Battle rages! You strike but the enemy retaliates!";
                        if (player.hp <= 0) {
                            message = "💀 Your adventure ends here... Press R to begin anew.";
                        }
                    }
                    return;
                }
            }
            
            player.x = nextX;
            player.y = nextY;
            message = "You tread carefully through the shadowy halls...";

            Item itemToPickup = null;
            for (Item item : items) {
                if (item.x == player.x && item.y == player.y) {
                    player.pickupItem(item);
                    if (item.name.equals("Health Elixir")) {
                        message = "✨ You discovered a healing elixir! (+20 HP)";
                    } else if (item.name.equals("Power Crystal")) {
                        message = "💎 A power crystal enhances your abilities! (+5 Attack)";
                    }
                    itemToPickup = item;
                    break;
                }
            }
            if (itemToPickup != null) {
                items.remove(itemToPickup);
            }

            if (player.x == stairs.x && player.y == stairs.y) {
                nextLevel();
            }
        }

        private void updateMonsters() {
            for (Monster monster : monsters) {
                monster.moveTowards(player, map);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Enable antialiasing for smooth graphics
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawMap(g2d);
            drawShadows(g2d);
            drawEntities(g2d);
            drawEffects(g2d);
            drawUI(g2d);
        }

        private void drawMap(Graphics2D g2d) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                for (int y = 0; y < MAP_HEIGHT; y++) {
                    Rectangle2D tile = new Rectangle2D.Float(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    
                    if (map[x][y] == Tile.FLOOR) {
                        // Gradient floor with subtle variation
                        Color baseColor = FLOOR_COLORS[Math.abs((x + y) % FLOOR_COLORS.length)];
                        GradientPaint gradient = new GradientPaint(
                            x * TILE_SIZE, y * TILE_SIZE, baseColor,
                            (x + 1) * TILE_SIZE, (y + 1) * TILE_SIZE, 
                            new Color(baseColor.getRed() + 10, baseColor.getGreen() + 10, baseColor.getBlue() + 15)
                        );
                        g2d.setPaint(gradient);
                        g2d.fill(tile);
                        
                        // Subtle floor texture
                        g2d.setColor(new Color(255, 255, 255, 5));
                        if ((x + y) % 3 == 0) {
                            g2d.drawLine(x * TILE_SIZE, y * TILE_SIZE, 
                                        x * TILE_SIZE + TILE_SIZE/3, y * TILE_SIZE + TILE_SIZE/3);
                        }
                    } else {
                        // Enhanced walls with depth
                        Color baseWall = WALL_COLORS[Math.abs((x + y) % WALL_COLORS.length)];
                        GradientPaint wallGradient = new GradientPaint(
                            x * TILE_SIZE, y * TILE_SIZE, 
                            new Color(baseWall.getRed() + 15, baseWall.getGreen() + 15, baseWall.getBlue() + 20),
                            (x + 1) * TILE_SIZE, (y + 1) * TILE_SIZE, baseWall
                        );
                        g2d.setPaint(wallGradient);
                        g2d.fill(new RoundRectangle2D.Float(x * TILE_SIZE, y * TILE_SIZE, 
                                                          TILE_SIZE, TILE_SIZE, 4, 4));
                        
                        // Wall border for depth
                        g2d.setColor(new Color(baseWall.getRed() - 20, baseWall.getGreen() - 20, 
                                             baseWall.getBlue() - 25, 150));
                        g2d.setStroke(new BasicStroke(1.5f));
                        g2d.draw(new RoundRectangle2D.Float(x * TILE_SIZE + 1, y * TILE_SIZE + 1, 
                                                          TILE_SIZE - 2, TILE_SIZE - 2, 4, 4));
                    }
                }
            }
        }

        private void drawShadows(Graphics2D g2d) {
            if (shadowLayer != null) {
                g2d.drawImage(shadowLayer, 0, 0, null);
            }
        }

        private void drawEntities(Graphics2D g2d) {
            g2d.setFont(gameFont);
            
            // Animated stairs with pulsing glow
            drawEntityWithGlow(g2d, "◉", stairs.x, stairs.y, STAIRS_COLOR, STAIRS_GLOW, 1.2f);
            
            // Items with gentle glow
            for (Item item : items) {
                Color glowColor = item.name.equals("Power Crystal") ? 
                    new Color(180, 100, 255, 100) : ITEM_GLOW;
                drawEntityWithGlow(g2d, String.valueOf(item.symbol), item.x, item.y, 
                                 ITEM_COLOR, glowColor, 1.0f);
            }

            // Monsters with menacing glow
            for (Monster monster : monsters) {
                float pulse = 0.8f + 0.2f * (float)Math.sin(gameTime * 0.008);
                drawEntityWithGlow(g2d, "👹", monster.x, monster.y, MONSTER_COLOR, MONSTER_GLOW, pulse);
            }

            // Player with heroic glow
            if (player.hp > 0) {
                float heroGlow = 1.0f + 0.15f * (float)Math.sin(gameTime * 0.005);
                drawEntityWithGlow(g2d, "🛡", player.x, player.y, PLAYER_COLOR, PLAYER_GLOW, heroGlow);
            } else {
                // Player corpse
                drawEntityWithGlow(g2d, "💀", player.x, player.y, Color.GRAY, 
                                 new Color(100, 100, 100, 80), 1.0f);
            }
        }

        private void drawEntityWithGlow(Graphics2D g2d, String symbol, int x, int y, 
                                       Color color, Color glowColor, float intensity) {
            int pixelX = x * TILE_SIZE + TILE_SIZE / 2;
            int pixelY = y * TILE_SIZE + TILE_SIZE / 2;
            
            // Draw glow effect
            int glowSize = (int)(12 * intensity);
            for (int i = glowSize; i > 0; i--) {
                int alpha = Math.max(0, Math.min(255, (int)(glowColor.getAlpha() * (1.0f - (float)i/glowSize))));
                g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), 
                                     glowColor.getBlue(), alpha));
                g2d.fillOval(pixelX - i, pixelY - i, i * 2, i * 2);
            }
            
            // Draw entity
            g2d.setColor(color);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = pixelX - fm.stringWidth(symbol) / 2;
            int textY = pixelY + fm.getAscent() / 2 - 2;
            
            // Add subtle shadow to text
            g2d.setColor(Color.BLACK);
            g2d.drawString(symbol, textX + 1, textY + 1);
            g2d.setColor(color);
            g2d.drawString(symbol, textX, textY);
        }

        private void drawEffects(Graphics2D g2d) {
            // Ambient lighting effect
            RadialGradientPaint ambient = new RadialGradientPaint(
                player.x * TILE_SIZE + TILE_SIZE/2, 
                player.y * TILE_SIZE + TILE_SIZE/2,
                TILE_SIZE * 6,
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{
                    new Color(100, 150, 200, 15),
                    new Color(50, 100, 150, 8),
                    new Color(0, 0, 0, 0)
                }
            );
            g2d.setPaint(ambient);
            g2d.fillRect(0, 0, PANEL_WIDTH, MAP_HEIGHT * TILE_SIZE);
        }

        private void drawUI(Graphics2D g2d) {
            int uiY = MAP_HEIGHT * TILE_SIZE;
            int uiHeight = 120;
            
            // Main UI background with gradient
            GradientPaint uiBg = new GradientPaint(0, uiY, UI_BG, 0, uiY + uiHeight, 
                                                 new Color(UI_BG.getRed(), UI_BG.getGreen(), 
                                                          UI_BG.getBlue(), 240));
            g2d.setPaint(uiBg);
            g2d.fillRect(0, uiY, PANEL_WIDTH, uiHeight);
            
            // UI border
            g2d.setColor(UI_BORDER);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(0, uiY, PANEL_WIDTH - 1, uiHeight - 1);

            // Health bar
            drawHealthBar(g2d, 20, uiY + 20);
            
            // Stats
            g2d.setFont(titleFont);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Level " + dungeonLevel, 20, uiY + 70);
            g2d.drawString("Attack: " + player.attackPower, 150, uiY + 70);
            g2d.drawString("Potions: " + player.potionCount, 280, uiY + 70);
            g2d.drawString("Experience: " + player.experience, 420, uiY + 70);

            // Controls hint
            g2d.setFont(uiFont);
            g2d.setColor(new Color(200, 200, 200));
            String controls = player.hp <= 0 ? "Press R to restart" : "WASD/Arrows: Move | Space/H: Use Potion";
            g2d.drawString(controls, PANEL_WIDTH - 350, uiY + 70);
            
            // Message
            g2d.setFont(uiFont);
            g2d.setColor(new Color(255, 255, 150));
            g2d.drawString(message, 20, uiY + 95);
        }

        private void drawHealthBar(Graphics2D g2d, int x, int y) {
            int barWidth = 200;
            int barHeight = 20;
            float healthPercent = (float) player.hp / player.maxHp;
            
            // Background
            g2d.setColor(new Color(60, 60, 60));
            g2d.fillRoundRect(x, y, barWidth, barHeight, 10, 10);
            
            // Health fill with color coding
            Color healthColor;
            if (healthPercent > 0.6f) healthColor = HEALTH_HIGH;
            else if (healthPercent > 0.3f) healthColor = HEALTH_MED;
            else healthColor = HEALTH_LOW;
            
            GradientPaint healthGradient = new GradientPaint(
                x, y, healthColor,
                x, y + barHeight, new Color(healthColor.getRed() - 40, 
                                           healthColor.getGreen() - 40, 
                                           healthColor.getBlue() - 40)
            );
            g2d.setPaint(healthGradient);
            g2d.fillRoundRect(x, y, (int)(barWidth * healthPercent), barHeight, 10, 10);
            
            // Border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(x, y, barWidth, barHeight, 10, 10);
            
            // Health text
            g2d.setFont(titleFont);
            String healthText = player.hp + "/" + player.maxHp + " HP";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (barWidth - fm.stringWidth(healthText)) / 2;
            int textY = y + (barHeight + fm.getAscent()) / 2 - 2;
            
            g2d.setColor(Color.BLACK);
            g2d.drawString(healthText, textX + 1, textY + 1);
            g2d.setColor(Color.WHITE);
            g2d.drawString(healthText, textX, textY);
        }

        // Game entity classes
        enum Tile { WALL, FLOOR }

        class Player {
            int x, y;
            int hp, maxHp;
            int attackPower;
            int potionCount;
            int experience;

            Player(int x, int y) {
                this.x = x;
                this.y = y;
                this.maxHp = 100;
                this.hp = maxHp;
                this.attackPower = 12;
                this.potionCount = 3;
                this.experience = 0;
            }

            void attack(Monster target) {
                target.hp -= this.attackPower;
            }

            void pickupItem(Item item) {
                if (item.name.equals("Health Elixir")) {
                    this.potionCount++;
                    if (this.hp < this.maxHp * 0.7) {
                        usePotion();
                    }
                } else if (item.name.equals("Power Crystal")) {
                    this.attackPower += 5;
                }
            }

            void usePotion() {
                if (potionCount > 0 && hp < maxHp) {
                    this.hp += 35;
                    if (this.hp > this.maxHp) this.hp = this.maxHp;
                    this.potionCount--;
                    message = "✨ The elixir restores your vitality! (+35 HP)";
                } else if (potionCount <= 0) {
                    message = "You have no potions left!";
                } else {
                    message = "You are already at full health!";
                }
            }

            void gainExperience(int exp) {
                this.experience += exp;
                if (this.experience >= 50 && this.experience < 60) {
                    this.maxHp += 20;
                    this.hp += 20;
                    this.attackPower += 3;
                    message += " You feel stronger! (+20 Max HP, +3 Attack)";
                }
            }
        }

        class Monster {
            int x, y;
            int hp, maxHp;
            int attackPower;

            Monster(int x, int y, int hp, int attackPower) {
                this.x = x;
                this.y = y;
                this.maxHp = hp;
                this.hp = hp;
                this.attackPower = attackPower;
            }

            void attack(Player target) {
                target.hp -= this.attackPower;
            }

            void moveTowards(Player player, Tile[][] map) {
                int dx = Integer.compare(player.x, this.x);
                int dy = Integer.compare(player.y, this.y);

                int nextX = this.x + dx;
                int nextY = this.y + dy;

                if (nextX == player.x && nextY == player.y) {
                    attack(player);
                    message = "💥 The creature strikes with deadly force!";
                    if (player.hp <= 0) message = "💀 You have fallen in battle! Press R to try again.";
                    return;
                }

                if (nextX >= 0 && nextX < MAP_WIDTH && nextY >= 0 && nextY < MAP_HEIGHT) {
                    if (map[nextX][this.y] == Tile.FLOOR) {
                        this.x = nextX;
                    } else if (map[this.x][nextY] == Tile.FLOOR) {
                        this.y = nextY;
                    }
                }
            }
        }

        class Item {
            int x, y;
            String name;
            char symbol;

            Item(int x, int y, String name, char symbol) {
                this.x = x;
                this.y = y;
                this.name = name;
                this.symbol = symbol;
            }
        }

        // Map generation
        class MapGenerator {
            private final int width, height;
            private final Tile[][] map;
            private final List<Rectangle> rooms = new ArrayList<>();
            private final Random rand = new Random();

            public MapGenerator(int width, int height) {
                this.width = width;
                this.height = height;
                this.map = new Tile[width][height];
            }

            public Tile[][] generateMap() {
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        map[x][y] = Tile.WALL;
                    }
                }

                int maxRooms = 12;
                for (int i = 0; i < maxRooms; i++) {
                    int w = rand.nextInt(6) + 4;
                    int h = rand.nextInt(6) + 4;
                    int x = rand.nextInt(width - w - 1) + 1;
                    int y = rand.nextInt(height - h - 1) + 1;

                    Rectangle newRoom = new Rectangle(x, y, w, h);
                    boolean failed = false;
                    for (Rectangle otherRoom : rooms) {
                        if (newRoom.intersects(otherRoom)) {
                            failed = true;
                            break;
                        }
                    }
                    if (!failed) {
                        carveRoom(newRoom);
                        rooms.add(newRoom);
                    }
                }

                for (int i = 0; i < rooms.size() - 1; i++) {
                    Point center1 = rooms.get(i).getCenter();
                    Point center2 = rooms.get(i + 1).getCenter();
                    carveCorridor(center1, center2);
                }

                return map;
            }

            private void carveRoom(Rectangle room) {
                for (int x = room.x; x < room.x + room.width; x++) {
                    for (int y = room.y; y < room.y + room.height; y++) {
                        map[x][y] = Tile.FLOOR;
                    }
                }
            }

            private void carveCorridor(Point p1, Point p2) {
                int x = p1.x;
                int y = p1.y;

                while (x != p2.x) {
                    map[x][y] = Tile.FLOOR;
                    x += Integer.compare(p2.x, x);
                }
                while (y != p2.y) {
                    map[x][y] = Tile.FLOOR;
                    y += Integer.compare(p2.y, y);
                }
            }

            public List<Rectangle> getRooms() {
                return rooms;
            }

            public Point getRandomRoomCenter() {
                if (rooms.isEmpty()) return new Point(width / 2, height / 2);
                return rooms.get(rand.nextInt(rooms.size())).getCenter();
            }
        }

        class Rectangle extends java.awt.Rectangle {
            public Rectangle(int x, int y, int width, int height) {
                super(x, y, width, height);
            }

            public Point getCenter() {
                return new Point(x + width / 2, y + height / 2);
            }
        }
    }
}
