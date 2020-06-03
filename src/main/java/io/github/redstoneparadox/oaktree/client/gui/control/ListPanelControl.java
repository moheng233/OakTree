package io.github.redstoneparadox.oaktree.client.gui.control;

import io.github.redstoneparadox.oaktree.client.gui.ControlGui;
import io.github.redstoneparadox.oaktree.client.geometry.Vector2D;

public class ListPanelControl extends PanelControl<ListPanelControl> {
    public boolean horizontal = false;
    public int displayCount = 1;
    public int startIndex = 0;

    public ListPanelControl() {
        id = "list_panel";
    }

    public ListPanelControl horizontal(boolean horizontal) {
        this.horizontal = horizontal;
        return this;
    }

    // TODO: Display count should only be clamped during preDraw
    public ListPanelControl displayCount(int displayCount) {
        if (displayCount < 1) this.displayCount = 1;
        else this.displayCount = Math.min(displayCount, children.size());
        return this;
    }

    public ListPanelControl startIndex(int currentIndex) {
        if (currentIndex < 0) this.startIndex = 0;
        else this.startIndex = Math.min(currentIndex, children.size() - displayCount);
        return this;
    }

    public ListPanelControl scroll(int amount) {
        return startIndex(startIndex + amount);
    }

    @Override
    void arrangeChildren(ControlGui gui, int mouseX, int mouseY) {
        if (!horizontal) {
            int sectionHeight = area.height/displayCount;
            Vector2D innerDimensions = innerDimensions(area.width, sectionHeight);
            Vector2D innerPosition = innerPosition(trueX, trueY);

            for (int i = 0; i < displayCount; i += 1) {
                int entryY = innerPosition.y + (i * sectionHeight);

                Control<?> child = children.get(i + startIndex);
                if (child != null) child.preDraw(gui, innerPosition.x, entryY, innerDimensions.x, innerDimensions.y, mouseX, mouseY);
            }
        }
        else {
            int sectionWidth = area.width/displayCount;
            Vector2D innerDimensions = innerDimensions(sectionWidth, area.height);
            Vector2D innerPosition = innerPosition(trueX, trueY);

            for (int i = 0; i < displayCount; i += 1) {
                int entryX = innerPosition.x + (i * sectionWidth);

                Control<?> child = children.get(i + startIndex);
                if (child != null) child.preDraw(gui, entryX, innerPosition.y, innerDimensions.x, innerDimensions.y, mouseX, mouseY);
            }
        }
    }

    @Override
    boolean shouldDraw(Control<?> child) {
        int index = children.indexOf(child);
        return index >= startIndex && index < startIndex + displayCount;
    }
}
