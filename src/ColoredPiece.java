class ColoredPiece {
    private int color;
    private int x, y;

    public ColoredPiece(int color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public int getColor() { return color; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
