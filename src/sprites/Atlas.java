package sprites;

import java.util.List;

/**
 *
 * @author migo
 */
public class Atlas {

    private String image;
    private List<SpriteImage> Subtexture;

    public List<SpriteImage> getSubtexture() {
        return Subtexture;
    }

    public void setSubtexture(List<SpriteImage> Subtexture) {
        this.Subtexture = Subtexture;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Atlas{" + "image=" + image + ", Subtexture=" + Subtexture + '}';
    }

}
