package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.JsonIterator;
import com.badlogic.gdx.utils.ObjectMap;

public class AssetsGroup implements Disposable, AssetErrorListener {

    private static final String TAG = "Assets";
    private AssetManager manager;
    private ObjectMap<String, Array<JsonAsset>> groups;

    public AssetsGroup(String assetFile) {

        manager = new AssetManager();
        manager.setErrorListener(this);
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        loadGroups(assetFile);
    }

    public AssetLoader<?, ?> getLoader(Class<?> type) {
        return manager.getLoader(type);
    }

    public void loadGroup(String groupName) {
        Gdx.app.log(TAG, "loading group " + groupName);

        Array<JsonAsset> assets = groups.get(groupName, null);

        if (assets != null) {
            for (JsonAsset asset : assets) {
                Gdx.app.log(TAG, "loading..." + asset.path);
                manager.load(asset.path, asset.type, asset.parameters);
            }
        }
        else {
            Gdx.app.log(TAG, "error loading group " + groupName + ", not found");
        }
    }

    public void unloadGroup(String groupName) {
        Gdx.app.log(TAG, "unloading group " + groupName);

        Array<JsonAsset> assets = groups.get(groupName, null);

        if (assets != null) {
            for (JsonAsset asset : assets) {
                if (manager.isLoaded(asset.path, asset.type)) {
                    manager.unload(asset.path);
                }
            }
        }
        else {
            Gdx.app.log(TAG, "error unloading group " + groupName + ", not found");
        }
    }

    public synchronized <T> T get(String fileName) {
        return manager.get(fileName);
    }

    public synchronized <T> T get(String fileName, Class<T> type) {
        return manager.get(fileName, type);
    }

    public <T> boolean isLoaded(String fileName, Class<T> type) {
        return manager.isLoaded(fileName, type);
    }

    public boolean update() {
        return manager.update();
    }

    public void finishLoading() {
        manager.finishLoading();
    }

    public float getProgress() {
        return manager.getProgress();
    }

    @Override
    public void dispose() {
        Gdx.app.log(TAG, "shutting down");
        manager.dispose();
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.log(TAG, "error loading " + asset.fileName + " message: " + throwable.getMessage());
    }

    private void loadGroups(String assetFile) {
        groups = new ObjectMap<String, Array<JsonAsset>>();


        Gdx.app.log(TAG, "loading file " + assetFile);

        try {
            Json json = new Json();
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(Gdx.files.internal(assetFile));

            JsonIterator groupIt = root.iterator();

            while (groupIt.hasNext()) {
                JsonValue groupValue = groupIt.next();

                if (groups.containsKey(groupValue.name)) {
                    Gdx.app.log(TAG, "group " + groupValue.name + " already exists, skipping");
                    continue;
                }

                Gdx.app.log(TAG, "registering group " + groupValue.name);

                Array<JsonAsset> assets = new Array<JsonAsset>();

                JsonIterator assetIt = groupValue.iterator();

                while (assetIt.hasNext()) {
                    JsonValue assetValue = assetIt.next();

                    JsonAsset asset = json.fromJson(JsonAsset.class, assetValue.toString());
                    assets.add(asset);
                }

                groups.put(groupValue.name, assets);
            }
        }
        catch (Exception e) {
            Gdx.app.log(TAG, "error loading file " + assetFile + " " + e.getMessage());
        }
    }
}