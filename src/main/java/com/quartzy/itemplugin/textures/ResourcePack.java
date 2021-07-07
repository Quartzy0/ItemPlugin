package com.quartzy.itemplugin.textures;

import com.google.gson.*;
import com.quartzy.itemplugin.items.CustomItem;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.spi.FileSystemProvider;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ResourcePack{
    
    private static Path tmpDir = null;

    private HashMap<MinecraftKey, BlockModel> blockModels;
    private HashMap<MinecraftKey, ItemModel> itemModels;
    
    @Getter
    @Setter
    private String description;
    
    private byte[] sha1Hash;
    private Path packFile;
    private Path resourcePath;
    private Path texturesPath;
    
    private int customModelDataCount = 10000;
    
    private boolean needsRefreshing = false;
    
    public ResourcePack(String description, Path texturesPath, Path resourcePath){
        this.description = description;
        this.resourcePath = resourcePath;
        this.texturesPath = texturesPath;
        this.blockModels = new HashMap<>();
        this.itemModels = new HashMap<>();
    }
    
    public void regenerate(){
        if(this.packFile==null)return;
        this.generateZipFile(this.packFile);
    }
    
    @SneakyThrows(IOException.class)
    public void generateZipFile(Path fileOut){
        if(fileOut==null)
            throw new NullPointerException("File cannot be null");
        if(!Files.exists(fileOut)){
            fileOut.getParent().toFile().mkdirs();
        }else{
            if(!Files.isWritable(fileOut))
                throw new RuntimeException("Cannot write to file: " + fileOut);
            if(Files.isDirectory(fileOut))
                throw new RuntimeException(fileOut + " is directory when it should be a file");
            Files.delete(fileOut);
        }
        
        //Make zip file
        FileSystem zipFS = newFileSystem(fileOut);
        
        //Make sure tmp dir exists. This is where files are constructed before being copied to the zip file
        if(tmpDir==null){
            tmpDir = Files.createTempDirectory("item-plugin-resource-pack-construction");
        }
        
        //Generate pack.mcmeta
        String jsonPackString = "{\n\"pack\":{\n\"description\": \"" + description + "\",\"pack_format\": 6\n}\n}";
        Path pack = tmpDir.resolve("pack.mcmeta");
        Files.writeString(pack, jsonPackString, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        Files.move(pack, zipFS.getPath("/pack.mcmeta"), StandardCopyOption.REPLACE_EXISTING);
    
        JsonParser jsonParser = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        
        //Generate altered item models
        Path itemModelPathZip = zipFS.getPath("assets", "minecraft", "models", "item");
        Path itemModelPathTmp = tmpDir.resolve("assets/minecraft/models/item");
        createDirectory(itemModelPathTmp);
        createDirectory(itemModelPathZip);
        for(Map.Entry<MinecraftKey, ItemModel> entry : itemModels.entrySet()){
            JsonObject rootObject;
            Path itemPath = itemModelPathTmp.resolve(entry.getValue().item_override);
            if(Files.exists(itemPath)){
                rootObject = jsonParser.parse(Files.readString(itemPath)).getAsJsonObject();
            }else{
                Files.createFile(itemPath);
                InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("defaults/assets/minecraft/models/item/" + entry.getValue().item_override);
                if(resourceAsStream==null){
                    rootObject = jsonParser.parse("{}").getAsJsonObject();
                }else{
                    byte[] bytes = resourceAsStream.readAllBytes();
                    String jsonString = new String(bytes, StandardCharsets.UTF_8);
                    rootObject = jsonParser.parse(jsonString).getAsJsonObject();
                }
            }
            
            JsonObject override = new JsonObject();
            override.addProperty("model", entry.getValue().texture_name);
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", entry.getValue().custom_model_data);
            override.add("predicate", predicate);
            if(rootObject.has("overrides")){
                JsonArray overrides = rootObject.getAsJsonArray("overrides");
                overrides.add(override);
            }else{
                JsonArray overrides = new JsonArray();
                overrides.add(override);
                rootObject.add("overrides", overrides);
            }
            
            Files.writeString(itemPath, gson.toJson(rootObject), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        }
        copy(itemModelPathTmp, itemModelPathZip, StandardCopyOption.REPLACE_EXISTING);
        FileUtils.deleteDirectory(itemModelPathTmp.toFile());
        
        //Generate altered block models
        Path blockStatePathZip = zipFS.getPath("assets", "minecraft", "blockstates");
        Path blockModelPathZip = zipFS.getPath("assets", "minecraft", "models", "block");
        Path blockStatePathTmp = tmpDir.resolve("assets/minecraft/blockstates");
        Path blockModelPathUser = null;
        if(this.texturesPath!=null){
            blockModelPathUser = texturesPath.resolve("assets/minecraft/models/block");
        }
        Path blockModelPathTmp = tmpDir.resolve("assets/minecraft/models/block");
        createDirectory(blockModelPathTmp);
        createDirectory(blockModelPathZip);
        createDirectory(blockStatePathTmp);
        createDirectory(blockStatePathZip);
        for(Map.Entry<MinecraftKey, BlockModel> entry : blockModels.entrySet()){
            Path blockPath = blockStatePathTmp.resolve(entry.getValue().override_model);
            if(entry.getValue().useMultipart){
                JsonObject rootObject;
                if(Files.exists(blockPath)){
                    rootObject = jsonParser.parse(Files.readString(blockPath)).getAsJsonObject();
                }else{
                    Files.createFile(blockPath);
                    InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("defaults/assets/minecraft/blockstates" + entry.getValue().override_model);
                    if(resourceAsStream==null){
                        rootObject = jsonParser.parse("{}").getAsJsonObject();
                    }else{
                        byte[] bytes = resourceAsStream.readAllBytes();
                        String jsonString = new String(bytes, StandardCharsets.UTF_8);
                        rootObject = jsonParser.parse(jsonString).getAsJsonObject();
                    }
                }
                JsonObject multipart = new JsonObject();
                JsonObject when = new JsonObject();
                when.addProperty("down", entry.getValue().down);
                when.addProperty("up", entry.getValue().up);
                when.addProperty("north", entry.getValue().north);
                when.addProperty("south", entry.getValue().south);
                when.addProperty("west", entry.getValue().west);
                when.addProperty("east", entry.getValue().east);
                multipart.add("when", when);
                JsonObject apply = new JsonObject();
                apply.addProperty("model", entry.getValue().model);
                multipart.add("apply", apply);
                if(rootObject.has("multipart")){
                    rootObject.getAsJsonArray("multipart").add(multipart);
                } else{
                    JsonArray multipartArray = new JsonArray();
                    multipartArray.add(multipart);
                    rootObject.add("multipart", multipartArray);
                }
                
                Files.writeString(blockPath, gson.toJson(rootObject), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            }else if(blockModelPathUser!=null){
                Path userPath = blockModelPathUser.resolve(entry.getValue().model);
                if(!Files.exists(userPath)){
                    Bukkit.getLogger().warning("Model path " + userPath + " does not exist!");
                    continue;
                }
                Path userOverride = blockModelPathTmp.resolve(entry.getValue().override_model);
                Files.copy(userPath, userOverride, StandardCopyOption.REPLACE_EXISTING);
            }else{
                Bukkit.getLogger().warning("A path for the user's textures was not specified, yet a model is trying to be loaded from it! (" + entry.getValue().model + ")");
            }
        }
        copy(blockStatePathTmp, blockStatePathZip, StandardCopyOption.REPLACE_EXISTING);
        FileUtils.deleteDirectory(blockStatePathTmp.toFile());
        FileUtils.deleteDirectory(blockModelPathTmp.toFile());
        
        //Merge user's resource pack
        if(this.texturesPath!=null){
            if(!Files.exists(this.texturesPath))Files.createDirectories(this.texturesPath);
            else copyAllFiles(texturesPath, zipFS.getPath("/"), StandardCopyOption.REPLACE_EXISTING);
        }
        
        //Close file system
        zipFS.close();
    
        this.packFile = fileOut.normalize().toAbsolutePath();
        try{
            this.sha1Hash = calcSHA1(fileOut.toFile());
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }
    
    public void sendToPlayer(Player player){
        String s = "http://" + ResourceWebServer.getIp() + ":48391/resources/" + this.resourcePath.normalize().toAbsolutePath().relativize(this.packFile.normalize().toAbsolutePath());
        player.setResourcePack(s, this.sha1Hash);
    }
    
    public void addItemTexture(MinecraftKey item, String item_override, String item_texture, int customModelData){
        this.itemModels.put(item, new ItemModel(item_override + ".json", item.getNamespace() + ":" + item_texture, customModelData));
        this.needsRefreshing = true;
    }
    
    public void addItemTexture(MinecraftKey item, String item_override, String item_texture){
        addItemTexture(item, item_override, item_texture, customModelDataCount++);
    }
    
    public void addItemTexture(MinecraftKey item, String item_override){
        addItemTexture(item, item_override, "item/" + item.getKey());
    }
    
    public void addItemTexture(MinecraftKey item){
        addItemTexture(item, "carrot_on_a_stick");
    }
    
    public void addItemTexture(CustomItem item, String item_override){
        addItemTexture(item.getId(), item_override);
    }
    
    public void addItemTexture(CustomItem item){
        addItemTexture(item.getId());
    }
    
    public void addBlockTexture(MinecraftKey block, boolean down, boolean east, boolean north, boolean south, boolean up, boolean west, boolean useMultipart, String model, String override_model){
        this.blockModels.put(block, new BlockModel(down, east, north, south, up, west, useMultipart, model, override_model));
        this.needsRefreshing = true;
    }
    
    /**
     * Use for completely overriding a block model for another
     */
    public void addBlockTexture(MinecraftKey block, String model, String override_model){
        this.addBlockTexture(block, false, false, false, false, false, false, false, model, override_model);
    }
    
    public void addBlockTexture(MinecraftKey block, boolean down, boolean east, boolean north, boolean south, boolean up, boolean west, String model, String override_model){
        this.addBlockTexture(block, down, east, north, south, up, west, true, model, override_model);
    }
    
    public int getModelData(MinecraftKey id){
        if(id==null)return -1;
        ItemModel itemModel = this.itemModels.get(id);
        if(itemModel!=null)return itemModel.custom_model_data;
        return -1;
    }
    
    public int getModelData(CustomItem item){
        if(item==null)return -1;
        return this.getModelData(item.getId());
    }
    
    private static class BlockModel{
        public final boolean down, east, north, south, up, west;
        public final String model;
        public final String override_model;
        public final boolean useMultipart;
    
        public BlockModel(boolean down, boolean east, boolean north, boolean south, boolean up, boolean west, boolean useMultipart, String model, String override_model){
            this.down = down;
            this.east = east;
            this.north = north;
            this.south = south;
            this.up = up;
            this.west = west;
            this.model = model;
            this.override_model = override_model;
            this.useMultipart = useMultipart;
        }
    }
    
    private static class ItemModel{
        public final String item_override;
        public final String texture_name;
        public final int custom_model_data;
    
        public ItemModel(String item_override, String texture_name, int custom_model_data){
            this.item_override = item_override;
            this.texture_name = texture_name;
            this.custom_model_data = custom_model_data;
        }
    
    }
    
    private static FileSystem newFileSystem(Path path) throws IOException{
        if (path == null)
            throw new NullPointerException();
        Map<String,String> env = new HashMap<>();
        env.put("create", "true");
    
        // check installed providers
        for (FileSystemProvider provider: FileSystemProvider.installedProviders()) {
            try {
                return provider.newFileSystem(path, env);
            } catch (UnsupportedOperationException uoe) {
            }
        }
    
        throw new ProviderNotFoundException("Provider not found");
    }
    
    private static void createDirectory(Path path) throws IOException{
        if(path==null)return;
        if(Files.exists(path.normalize().toAbsolutePath()))return;
        Files.createDirectories(path.normalize().toAbsolutePath());
    }
    
    private static void copy(Path src, Path dest, CopyOption... options) throws IOException{
        createDirectory(dest);
        Files.list(src).forEach(path -> {
            try{
                if(!Files.isRegularFile(path))return;
                Path resolve = dest.resolve(path.getFileName().toString());
                if(!Files.exists(resolve))Files.createFile(resolve);
                Files.copy(path, resolve, options);
            } catch(IOException e){
                e.printStackTrace();
            }
        });
    }
    
    public static void copyAllFiles(Path src, Path dest, CopyOption... options) throws IOException{
        createDirectory(dest);
        Files.list(src).forEach(path -> {
            try{
                Path resolve = dest.resolve(path.getFileName().toString());
                if(Files.isDirectory(path)){
                    copyAllFiles(path, resolve, options);
                    return;
                }
                Files.copy(path, resolve, options);
            } catch(IOException e){
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Read the file and calculate the SHA-1 checksum
     *
     * @param file
     *            the file to read
     * @return the hex representation of the SHA-1 using uppercase chars
     * @throws FileNotFoundException
     *             if the file does not exist, is a directory rather than a
     *             regular file, or for some other reason cannot be opened for
     *             reading
     * @throws IOException
     *             if an I/O error occurs
     * @throws NoSuchAlgorithmException
     *             should never happen
     */
    //https://stackoverflow.com/a/30925550/9472096
    private static byte[] calcSHA1(File file) throws FileNotFoundException,
            IOException, NoSuchAlgorithmException{
        
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        try (InputStream input = new FileInputStream(file)) {
            
            byte[] buffer = new byte[8192];
            int len = input.read(buffer);
            
            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }
            
            return sha1.digest();
        }
    }
}
