<p align="center">
  <img src="logo.svg" height=250>
</p>

<p align="center">
  <a href="https://travis-ci.com/github/hhu-bsinfo/infinileap"><img src="https://www.travis-ci.com/hhu-bsinfo/infinileap.svg?branch=master"></a>
  <a href="https://openjdk.java.net/projects/jdk/19/"><img src="https://img.shields.io/badge/java-19-blue.svg"></a>
  <a href="https://github.com/openucx/ucx/tree/v1.11.2"><img src="https://img.shields.io/badge/ucx-1.11.2-red.svg"></a>
  <a href="https://github.com/hhu-bsinfo/infinileap/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-GPLv3-orange.svg"></a>
  
</p>

<p align="center">
  <a href="https://www.ej-technologies.com/products/jprofiler/overview.html"><img src="https://www.ej-technologies.com/images/product_banners/jprofiler_large.png"></a>
</p>

Developed by the [operating systems group](https://www.cs.hhu.de/en/research-groups/operating-systems.html) of the department of computer science of the [Heinrich Heine University Düsseldorf](https://www.hhu.de), Infinileap aims at providing a simple object-oriented interface between Java and the native [`ucx`](https://github.com/openucx/ucx) library by leveraging Project Panama's [Foreign Function Interface](https://openjdk.java.net/jeps/191) and [Foreign-Memory Access API](https://openjdk.java.net/jeps/370).

## :construction: &nbsp; Notice

Infinileap is a research project under development. We do not recommend using the system in a production environment. Expect to encounter bugs. However, we are looking forward to bug reports and code contributions.

## :rocket: &nbsp; Demos

The project includes several sample applications that can be run from the command line.
In order to run them, a distribution of the [`example`](./example) module must first be created using Gradle.
By default, Gradle installs the distribution inside the module's `build` folder. This behavior can be customized
with the `installPath` property

``` 
# Install the distribution inside ${HOME}/infinileap
./gradlew example:installDist -PinstallPath="${HOME}/infinileap"

# Switch into ${HOME}/infinileap
cd "${HOME}/infinileap"
```

After Gradle has completed the build, the examples can be run from within the specified location.

All demos require a server as well as a client and should therefore be executed in two separate console windows.
The addresses to be used for establishing the connection can be specified via the following program arguments.

| Name               | Type                         | Side     | Description                             | Example          |
|--------------------|------------------------------|----------|-----------------------------------------|------------------|
| `-c` / `--connect` | `java.net.InetSocketAddress` | `CLIENT` | The server's address and port           | `127.0.0.1:2998` |
| `-l` / `--listen`  | `java.net.InetSocketAddress` | `SERVER` | The address the server should listen on | `127.0.0.1:2998` |

It is also possible to omit the port. In this case the default value `2998` is used.

### Messaging Demo ([Source](./example/src/main/java/de/hhu/bsinfo/infinileap/example/demo/Messaging.java))

This demo exchanges a message between the server and the client, which is then output to the console.

* **Server Side**

  ```console
  ./bin/infinileap messaging --listen 127.0.0.1
  ```

* **Client Side**

  ```console
  ./bin/infinileap messaging --connect 127.0.0.1
  ```

### Streaming Demo ([Source](./example/src/main/java/de/hhu/bsinfo/infinileap/example/demo/Streaming.java))

This demo exchanges a stream of data between the server and the client, which is then output to the console.

* **Server Side**

  ```console
  ./bin/infinileap streaming --listen 127.0.0.1
  ```

* **Client Side**

  ```console
  ./bin/infinileap streaming --connect 127.0.0.1
  ```

### Memory Access Demo ([Source](./example/src/main/java/de/hhu/bsinfo/infinileap/example/demo/Memory.java))

This demo directly reads the contents of a buffer residing inside the remote server's memory and prints it out to the console.

* **Server Side**

  ```console
  ./bin/infinileap memory --listen 127.0.0.1
  ```

* **Client Side**

  ```console
  ./bin/infinileap memory --connect 127.0.0.1
  ```

### Atomic Operation Demo ([Source](./example/src/main/java/de/hhu/bsinfo/infinileap/example/demo/Atomic.java))

This demo atomically increments a value residing inside the remote server's memory and prints out the result to the console.

* **Server Side**

  ```console
  ./bin/infinileap atomic --listen 127.0.0.1
  ```

* **Client Side**

  ```console
  ./bin/infinileap atomic --connect 127.0.0.1
  ```

## :warning: &nbsp; Known issues

  - If your JDK is not installed in one of the default locations, Gradle can be instructed to look in a custom location. To enable this feature the `org.gradle.java.installations.paths` property has to be set within your global `gradle.properties` file usually located inside `${HOME}/.gradle`.
    
    ```
    org.gradle.java.installations.paths=/custom/path/jdk19
    ```
    
  - The HotSpot VM uses the SIGSEGV signal for its own purposes, which may interfere with signal handlers installed by the ucx library. Fortunately, ucx's signal handlers can be disabled by using an undocumented environment variable (see [MPI.jl issue #337](https://github.com/JuliaParallel/MPI.jl/issues/337#issuecomment-578377458)).

    ```
    UCX_ERROR_SIGNALS=""
    ```
    
  - The ucx library may fail at parsing some locale-dependent configuration values (e.g. numbers with decimal separators).

    ```
    parser.c:928  UCX  ERROR Invalid value for MEM_REG_GROWTH: '0.06ns'. Expected: time value: <number>[s|us|ms|ns]
    uct_md.c:270  UCX  ERROR Failed to read MD config
    ```

    Setting the locale to English might fix this.

## :wrench: &nbsp; Requirements

  * [OpenJDK 19 + Project Panama](https://github.com/openjdk/panama-foreign/tree/foreign-jextract)
      
    > We provide nightly builds of the `foreign-jextract` branch, which are compatible with [SDKMAN!](https://sdkman.io).
    > 
    > After installing SDKMAN! you can execute the following commands to install and use the latest nightly build.
    > 
    > 
    > ```
    > curl -s "https://coconucos.cs.hhu.de/forschung/jdk/install" | bash
    > sdk use java panama
    > ```
    
  * [ucx 1.11.2](https://github.com/openucx/ucx/releases/tag/v1.11.2)
  
## :scroll: &nbsp; License

This project is licensed under the GNU GPLv3 License - see the [LICENSE](LICENSE) file for details.
