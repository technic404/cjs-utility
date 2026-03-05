package pl.technic404.cjs;

import java.util.List;
import java.util.function.Function;

public class Constants {

    static final List<Function<String, List<String>>> METHODS = List.of(
            (e) -> List.of("forms", ": CjsForm[]"),
            (e) -> List.of("components", ": CjsComponentsCollection"),
            (e) -> List.of("toForms", "(element: HTMLElement): CjsForm[]"),
            (e) -> List.of("withData", "(data: " + e + "): this"),
            (e) -> List.of("withStyle", "(style: CjsStyleProperties): this"),
            (e) -> List.of("render", "(data: " + e + "): string"),
            (e) -> List.of("visualise", "(data: " + e + "): HTMLElement"),
            (e) -> List.of("setData", "(data: " + e + "): this"),
            (e) -> List.of("loadLayout", "(...layouts: CjsLayout[]): this"),
            (e) -> List.of("rerenderOnSearch", "(data: { useSmartRender: boolean }): this"),
            (e) -> List.of("rerenderComponents", "(data: " + e + ", options: { useSmartRender: boolean }): this"),
            (e) -> List.of("onLoad", "(callback: () => void): void"),
            (e) -> List.of("hide", "(): void"),
            (e) -> List.of("show", "(): void"),
            (e) -> List.of("setDefaultData", "(data: " + e + "): this"),
            (e) -> List.of("querySelector", "(...selectors: string[]): HTMLElement|null|HTMLElement[]"),
            (e) -> List.of("querySelectorAll", "(...selectors: string[]): HTMLElement[]|Element[]"),
            (e) -> List.of("exists", "(): boolean"),
            (e) -> List.of("fillHeight", "(offset: number, maxHeight: number|undefined): void")
        );
}
