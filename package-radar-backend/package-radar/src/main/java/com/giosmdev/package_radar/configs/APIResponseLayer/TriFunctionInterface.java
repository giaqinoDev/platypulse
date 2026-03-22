package com.giosmdev.package_radar.configs.APIResponseLayer;

@FunctionalInterface
public interface TriFunctionInterface<arg_one, arg_two, return_arg> {
    return_arg apply(arg_one a, arg_two b);
}
