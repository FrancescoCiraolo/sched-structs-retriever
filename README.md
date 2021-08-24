# Sched Structs Retriever

**Note!** This software is now compatible with last bbc version, 2.9.1, and last
linux kernel versions, 5.11.0-25 and 5.11.0-31, for sure.
It is required more test, and probably fixes for other versions.

This library provides a way to get a picture of linux kernel sched domains structs.

Uses Sasha Goldshtein's trace script, part of bcc tools, to obtain values from runtime invocation of kernel sources.

## Usage

This library in mainly usable as component but is provided a simple executable test, as example. 

For using this library are

- trace, part of bcc, available
    - in the default repository
    - from sources https://github.com/iovisor/bcc

Linux kernel's source is no longer required.

## Build

Is required ant for build this project.

Is possible to make the library jar using `ant library` or an executable test with `ant example`.

## Example

Executing example, after it's been built, requires just to type `java -jar example.jar kernel_source_path [bcc_path]` replacing kernel_source_path and, optionally, the bcc_path with their actual path.

`java -jar example.jar`

## Background

I wrote this library during degree research with [Voxeldoodle](https://github.com/Voxeldoodle) under the  guidance of our relator [ebni](https://github.com/ebni) and of [dfaggioli](https://github.com/dfaggioli).

I also want to thank [yonghong-song](https://github.com/yonghong-song) who helped me to solve a version incompatibility with the last versions of the kernel