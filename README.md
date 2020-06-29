### [![Donate with Bitcoin](https://en.cryptobadges.io/badge/micro/3AijtcgVtaV7uKhwUibjSribPF7L13dJzP)](https://en.cryptobadges.io/donate/3AijtcgVtaV7uKhwUibjSribPF7L13dJzP)

# Sched Structs Retriever

This library provides a way to get a picture of linux kernel sched domains structs.

Uses Sasha Goldshtein's trace script, part of bcc tools, to obtain values from runtime invocation of kernel sources.

## Usage

This library in mainly usable as component but is provided a simple executable test, as example. 

For using this library are

- trace, part of bcc, available
    - in the default repository
    - from sources https://github.com/iovisor/bcc
- linux kernel source
    - https://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git/refs/

## Build

Is required ant for build this project.

Is possible to make the library jar using `ant library` or an executable test with `ant example`.

## Example

Executing example, after it's been built, requires just to type `java -jar example.jar kernel_source_path [bcc_path]` replacing kernel_source_path and, optionally, the bcc_path with their actual path.

`java -jar example.jar ~/linux-5.3`

## Background

I wrote this library during degree research with [Voxeldoodle](https://github.com/Voxeldoodle) under the  guidance of our relator [ebni](https://github.com/ebni) and of [dfaggioli](https://github.com/dfaggioli).
