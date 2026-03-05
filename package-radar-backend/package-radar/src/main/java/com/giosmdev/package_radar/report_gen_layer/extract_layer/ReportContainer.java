package com.giosmdev.package_radar.report_gen_layer.extract_layer;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReportContainer {
    Object extract(Object rawResponse);
}
