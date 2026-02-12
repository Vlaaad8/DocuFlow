import {
  HighContrastModeDetector
} from "./chunk-2ET2YIZY.js";
import {
  BidiModule
} from "./chunk-I77MDKE2.js";
import {
  InjectionToken,
  NgModule,
  inject,
  setClassMetadata,
  ɵɵdefineInjector,
  ɵɵdefineNgModule
} from "./chunk-BF7FI6KT.js";

// node_modules/@angular/material/fesm2022/common-module.mjs
var MATERIAL_SANITY_CHECKS = new InjectionToken("mat-sanity-checks", {
  providedIn: "root",
  factory: () => true
});
var MatCommonModule = class _MatCommonModule {
  constructor() {
    inject(HighContrastModeDetector)._applyBodyHighContrastModeCssClasses();
  }
  static ɵfac = function MatCommonModule_Factory(__ngFactoryType__) {
    return new (__ngFactoryType__ || _MatCommonModule)();
  };
  static ɵmod = ɵɵdefineNgModule({
    type: _MatCommonModule,
    imports: [BidiModule],
    exports: [BidiModule]
  });
  static ɵinj = ɵɵdefineInjector({
    imports: [BidiModule, BidiModule]
  });
};
(() => {
  (typeof ngDevMode === "undefined" || ngDevMode) && setClassMetadata(MatCommonModule, [{
    type: NgModule,
    args: [{
      imports: [BidiModule],
      exports: [BidiModule]
    }]
  }], () => [], null);
})();

export {
  MatCommonModule
};
//# sourceMappingURL=chunk-DZJDCMZS.js.map
