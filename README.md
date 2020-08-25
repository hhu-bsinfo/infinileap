<p align="center">
  <img src="logo.svg" height=250>
</p>

<p align="center">
  <a href="https://openjdk.java.net/projects/jdk/16/"><img src="https://img.shields.io/badge/java-16-blue.svg"></a>
  <a href="https://github.com/hhu-bsinfo/infinileap/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-GPLv3-orange.svg"></a>
</p>

<p align="center">
  <a href="https://www.ej-technologies.com/products/jprofiler/overview.html"><img src="https://www.ej-technologies.com/images/product_banners/jprofiler_large.png"></a>
</p>

Developed by the [operating systems group](https://www.cs.hhu.de/en/research-groups/operating-systems.html) of the department of computer science of the [Heinrich Heine University Düsseldorf](https://www.hhu.de), Infinileap aims at providing a simple object-oriented interface between Java and the native [`libibverbs`](https://github.com/linux-rdma/rdma-core/tree/master/libibverbs) library by leveraging Project Panama's [Foreign Function Interface](https://openjdk.java.net/jeps/191) and [Foreign-Memory Access API](https://openjdk.java.net/jeps/370).

## :construction: &nbsp; Notice

Infinileap is a research project under development. We do not recommend using the system in a production environment. Expect to encounter bugs. However, we are looking forward to bug reports and code contributions.

## :warning: &nbsp; Known issues

  - Gradle (currently at 6.6) does not yet support OpenJDK 16 ([#13481](https://github.com/gradle/gradle/issues/13481)). The [`gradle-jextract`](https://github.com/krakowski/gradle-jextract) plugin can work around this by using a different JDK for compiling the sources. To enable this feature the `javaHome` property has to be set within your global `gradle.properties` usually located inside `${HOME}/.gradle`.
  
    ```
    javaHome=/path/to/your/panama/java/home
    ```

## :wrench: &nbsp; Requirements

  * [OpenJDK 16 + Project Panama](https://github.com/openjdk/panama-foreign/tree/foreign-jextract)
  * [rdma-core 28.0](https://github.com/linux-rdma/rdma-core)
  
## :scroll: &nbsp; License

This project is licensed under the GNU GPLv3 License - see the [LICENSE](LICENSE) file for details.
