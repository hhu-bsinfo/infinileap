<p align="center">
  <img src="logo.svg" height=250>
</p>

<p align="center">
  <a href="https://openjdk.java.net/projects/jdk/16/"><img src="https://img.shields.io/badge/java-17-blue.svg"></a>
<a href="https://github.com/openucx/ucx/tree/v1.9.0"><img src="https://img.shields.io/badge/ucx-1.9.0-red.svg"></a>
  <a href="https://github.com/hhu-bsinfo/infinileap/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-GPLv3-orange.svg"></a>
  
</p>

<p align="center">
  <a href="https://www.ej-technologies.com/products/jprofiler/overview.html"><img src="https://www.ej-technologies.com/images/product_banners/jprofiler_large.png"></a>
</p>

Developed by the [operating systems group](https://www.cs.hhu.de/en/research-groups/operating-systems.html) of the department of computer science of the [Heinrich Heine University Düsseldorf](https://www.hhu.de), Infinileap aims at providing a simple object-oriented interface between Java and the native [`ucx`](https://github.com/openucx/ucx) library by leveraging Project Panama's [Foreign Function Interface](https://openjdk.java.net/jeps/191) and [Foreign-Memory Access API](https://openjdk.java.net/jeps/370).

## :construction: &nbsp; Notice

Infinileap is a research project under development. We do not recommend using the system in a production environment. Expect to encounter bugs. However, we are looking forward to bug reports and code contributions.

## :warning: &nbsp; Known issues

  - Gradle (currently at 6.8.3) does not yet support OpenJDK 16+ ([#13481](https://github.com/gradle/gradle/issues/13481)). The [`gradle-jextract`](https://github.com/krakowski/gradle-jextract) plugin can work around this by using a different JDK for compiling the sources. To enable this feature the `javaHome` property has to be set within your global `gradle.properties` usually located inside `${HOME}/.gradle`.
  
    ```
    javaHome=/path/to/your/panama/java/home
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

  * [OpenJDK 17 + Project Panama](https://github.com/openjdk/panama-foreign/tree/foreign-jextract)
  * [ucx 1.9.0](https://github.com/openucx/ucx/releases/tag/v1.9.0)
  
## :scroll: &nbsp; License

This project is licensed under the GNU GPLv3 License - see the [LICENSE](LICENSE) file for details.
