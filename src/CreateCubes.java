import com.sun.j3d.utils.universe.*;
import java.awt.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import javax.swing.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.keyboard.*;

public class CreateCubes extends JApplet {

	private static final long serialVersionUID = 1L;
	public Canvas3D c1 = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
    private SimpleUniverse u = null;
    private BranchGroup scene = null;

    public static int stateHeight = 5;
    public static int stateWidth = 5;
    public int stateDepth;
    private static float cubeHeight  = 0.1f;
    private static float cubeWidth = 0.1f;
    private static float cubeDepth = 0.1f;
    private static Random rn = new Random();
    private static Appearance appearanceBlack, appearanceWhite, appearanceBlue, appearanceRed;

    public com.sun.j3d.utils.geometry.Box[][][] cubes;
    public int[][][] currentBitString;

    public CreateCubes() {
        stateDepth = Menu.stateDepth;
        cubes = new com.sun.j3d.utils.geometry.Box [stateHeight][stateWidth][stateDepth];
        currentBitString = new int [stateHeight][stateWidth][stateDepth];
        setLayout(new BorderLayout());
        add("Center", c1);
        u = new SimpleUniverse(c1);
        scene = addCubesToGroup();
        scene.addChild(createLight(new Vector3f(4.0f, 0.0f, -1.0f)));
        u.addBranchGraph(scene);
    };

    public BranchGroup addCubesToGroup() {

        BranchGroup objRoot = new BranchGroup();
        Transform3D view = new Transform3D();
        //Sets the Cube in the centre of the page
        view.setTranslation(new Vector3f(0.0f, 0.0f, ((stateDepth*cubeDepth)*-2)-5));
        TransformGroup transformScene = new TransformGroup(view);
        transformScene.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformScene.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        //Creates Appearances for each colour.
        appearanceBlack = new Appearance();
        appearanceWhite = new Appearance();
        appearanceBlue = new Appearance();
        appearanceRed = new Appearance();
        //Creates the colours needed for each appearance.
        Color3f black = new Color3f(0.05f, 0.05f, 0.05f);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f darkGrey = new Color3f(0.2f, 0.2f, 0.2f);
        Color3f lightGrey = new Color3f(0.8f, 0.8f, 0.8f);
        Color3f lightBlue = new Color3f(0.0f, 0.0f, 0.7f);
        Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
        Color3f lightRed = new Color3f(0.7f, 0.0f, 0.0f);
        Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
        //Assigns the colour to the appearance.
        appearanceBlack.setMaterial(new Material(black, darkGrey, black, black, 1.0f));
        appearanceWhite.setMaterial(new Material(white, lightGrey, white, white, 1.0f));
        appearanceBlue.setMaterial(new Material(blue, lightBlue, blue, blue, 1.0f));
        appearanceRed.setMaterial(new Material(red, lightRed, red, red, 1.0f));

        // for loop start
        for (int x = 0; x  < stateHeight; x++) {
            for (int y = 0; y  < stateWidth; y++) {
                for (int z = 0; z  < stateDepth; z++) {
                    currentBitString[x][y][z] = 0;
                    cubes[x][y][z] = new com.sun.j3d.utils.geometry.Box(cubeHeight, cubeWidth, cubeDepth, appearanceWhite);
                }
            }
        }
        int numOfFlippedBits = 0;
        while(numOfFlippedBits < stateDepth){
            int i = rn.nextInt(5);
            int j = rn.nextInt(5);
            int k = rn.nextInt(stateDepth);
            if(currentBitString[i][j][k] == 0){
                currentBitString[i][j][k] = 1;
                cubes[i][j][k] = new com.sun.j3d.utils.geometry.Box(cubeHeight, cubeWidth, cubeDepth, appearanceBlack);
                numOfFlippedBits += 1;
            }
        }
        for (int x = 0; x  < stateHeight; x++) {
            for (int y = 0; y  < stateWidth; y++) {
                for (int z = 0; z  < stateDepth; z++) {
                    cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.FRONT).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                    cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.BACK).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                    cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.LEFT).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                    cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.RIGHT).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                    cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.TOP).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                    cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.BOTTOM).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                    TransformGroup transformCubes = new TransformGroup();
                    Transform3D transformVectors = new Transform3D();
                    Vector3f vector = new Vector3f( cubeHeight * (-2 * stateHeight + 2 + 4 * x), cubeWidth * (2 * stateWidth - 2 - 4 * y), cubeDepth * (2 * stateDepth - 2 - 4 * z));
                    transformVectors.setTranslation(vector);
                    transformCubes.setTransform(transformVectors);
                    transformCubes.addChild(cubes[x][y][z]);
                    transformScene.addChild(transformCubes);
                }
            }
        }
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        MouseRotate mouseRotBeh = new MouseRotate();
        mouseRotBeh.setTransformGroup(transformScene);
        mouseRotBeh.setSchedulingBounds(bounds);
        transformScene.addChild(mouseRotBeh);

        // Create the translate behavior node
        MouseTranslate mouseTransBeh = new MouseTranslate();
        mouseTransBeh.setTransformGroup(transformScene);
        mouseTransBeh.setSchedulingBounds(bounds);
        transformScene.addChild(mouseTransBeh);

        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(transformScene);
        keyNavBeh.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
        transformScene.addChild(keyNavBeh);
        objRoot.addChild(transformScene);

        return objRoot;
    };

    public DirectionalLight createLight(Vector3f lightDirection) {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        DirectionalLight light = new DirectionalLight(white, lightDirection);
        light.setInfluencingBounds(bounds);
        return light;
    };

    public void changeCubeColor(int x, int y, int z, Appearance appearanceToApply) {
        cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.FRONT).setAppearance(appearanceToApply);
        cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.BACK).setAppearance(appearanceToApply);
        cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.LEFT).setAppearance(appearanceToApply);
        cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.RIGHT).setAppearance(appearanceToApply);
        cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.TOP).setAppearance(appearanceToApply);
        cubes[x][y][z].getShape(com.sun.j3d.utils.geometry.Box.BOTTOM).setAppearance(appearanceToApply);
    };

    public void displayState(int[][][] state) {
        for (int x = 0; x < stateWidth; x++) {
            for (int y = 0; y < stateHeight; y++) {
                for (int z = 0; z < stateDepth; z++) {
                    if (state[x][y][z] == 1) {
                        changeCubeColor( x, y, z, appearanceBlack);
                    } else {
                        changeCubeColor( x, y, z, appearanceWhite);
                    }
                }
            }
        }
    };

    public void displayStateDifferences(int[][][] state1, int[][][] state2) {
        for (int x = 0; x < stateWidth; x++) {
            for (int y = 0; y < stateHeight; y++) {
                for (int z = 0; z < stateDepth; z++) {
                    if (state1[x][y][z] == state2[x][y][z]) {
                        changeCubeColor( x, y, z, appearanceBlue);
                    } else {
                        changeCubeColor( x, y, z, appearanceRed);
                    }
                }
            }
        }
    };

    public void colourStateRed(int round, int x, int y, int z, int numOfChanges, int numOfRounds, int min, int max) {
        Appearance redAppearance = new Appearance();
        int range = max - min;
        float roundsCompleted = (float) round / (float) (numOfRounds - 1);
        float shadeOfRed;
        if (range != 0) {
            shadeOfRed = 0.5f * (1.0f - (roundsCompleted * (((float) numOfChanges - (float) min) / (float) range)));
        } else {
            shadeOfRed = 0.5f;
        }
        Color3f red = new Color3f(1.0f, shadeOfRed, shadeOfRed);
        Color3f redShadow = new Color3f(0.85f, shadeOfRed, shadeOfRed);
        redAppearance.setMaterial(new Material(red, redShadow, red, red, 1.0f));
        changeCubeColor( x, y, z, redAppearance);
    };

    public void colourStateBlue(int round, int x, int y, int z, int numOfChanges, int numOfRounds, int min, int max) {
        Appearance blueApearance = new Appearance();
        int range = max - min;
        // float roundsCompleted = (float) round / (float) (numOfRounds - 1);
        float shadeOfBlue;
        if (range != 0) {
            shadeOfBlue = 0.5f * (1.0f - ((((float) numOfChanges - (float) min) / (float) range)));
        } else {
            shadeOfBlue = 0.5f;
        }
        Color3f blue = new Color3f(shadeOfBlue, shadeOfBlue, 1.0f);
        Color3f blueShadow = new Color3f(shadeOfBlue, shadeOfBlue, 0.85f);
        blueApearance.setMaterial(new Material(blue, blueShadow, blue, blue, 1.0f));
        changeCubeColor( x, y, z, blueApearance);
    };

}