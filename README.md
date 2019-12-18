# Oak Tree

[ ![Download](null/packages/redstoneparadox/mods/OakTree/images/download.svg?version=0.1.3-alpha) ](https://bintray.com/redstoneparadox/mods/OakTree/0.1.3-alpha/link)

Oak Tree is a GUI toolkit for use in Modded Minecraft alongside the Fabric modding API.

### Adding Oak Tree To Your Project

build.gradle:
```gradle
repositories {
    maven {
        url = "https://dl.bintray.com/redstoneparadox/mods"
    }
}

dependencies {
    modApi("io.github.redstoneparadox:OakTree:${project.oak_tree_version}") {
        exclude group: 'net.fabricmc.fabric-api'
    }
    include "io.github.redstoneparadox:OakTree:${project.oak_tree_version}"
}
```
