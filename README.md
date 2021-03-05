# HytoraCloud
Open source Minecraft Server management


HytoraCloud is a project that has taken the last 10 months of my life.
HytoraCloud is used to manage all of your BungeeCord and Spigot servers together in one big process.

Compatible Network Softwares:

  > Spigot : bukkit, paperSpigot
  > BungeeCord: bungeeCord, waterFall
  
Cloning project:

1. Clone the project from GitHub
![image](https://user-images.githubusercontent.com/63949927/110113639-af623900-7db3-11eb-9ab6-7c9c3fd8b502.png)

2. Add Vson as Library to the project
![image](https://user-images.githubusercontent.com/63949927/110113737-d02a8e80-7db3-11eb-9295-474f6bca01c5.png)

3. Checking for lombok (Make sure you have lombok plugin installed)
![image](https://user-images.githubusercontent.com/63949927/110113783-e5072200-7db3-11eb-9b7e-4c5c1c6a026d.png)

4. Adding artifacts (Only extract compile output and vson the other libraries are loading intern)
  > Also make sure that the output directory of the CloudAPI is the resources folder of the CLoudSystem ("/resources/implements/plugins/CloudAPI.jar")
![image](https://user-images.githubusercontent.com/63949927/110113841-fb14e280-7db3-11eb-9a32-3b965e4e8fb3.png)

5.Building and exporting
  > First build the CloudAPI then rebuild the CloudSystem and finally build the CloudSystem
![image](https://user-images.githubusercontent.com/63949927/110113963-31526200-7db4-11eb-94e0-783a46eaac38.png) 
![image](https://user-images.githubusercontent.com/63949927/110114057-4f1fc700-7db4-11eb-9ddd-48df9ad15a8c.png)


