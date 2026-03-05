package pl.technic404.cjs;

import java.util.List;
import java.util.function.Function;

public class Constants {

    static final List<Function<String, List<String>>> METHODS = List.of(
            (e) -> List.of("withData", "(data: " + e + "): this"),
            (e) -> List.of("render", "(data: " + e + "): this"),
            (e) -> List.of("visualise", "(data: " + e + "): this")
    );
}
