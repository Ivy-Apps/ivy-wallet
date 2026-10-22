{
  description = "Ivy Wallet dev shell: the minimum to build and test the app (no emulator)";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachSystem [ "x86_64-linux" "aarch64-darwin" "x86_64-darwin" ] (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config = {
            allowUnfree = true;
            android_sdk.accept_license = true;
          };
        };

        buildToolsVersion = "35.0.0";

        androidSdk = (pkgs.androidenv.composeAndroidPackages {
          platformVersions = [ "35" "36" ];
          buildToolsVersions = [ buildToolsVersion ];
          includeEmulator = false;
          includeSystemImages = false;
          includeNDK = false;
        }).androidsdk;

        sdkRoot = "${androidSdk}/libexec/android-sdk";
      in
      {
        devShells.default = pkgs.mkShell {
          packages = [ pkgs.jdk17 androidSdk ];

          JAVA_HOME = pkgs.jdk17.home;
          ANDROID_HOME = sdkRoot;
          ANDROID_SDK_ROOT = sdkRoot;
          # The aapt2 that Gradle downloads from Maven is dynamically linked and
          # does not run on NixOS, so use the one from the Nix SDK.
          GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${sdkRoot}/build-tools/${buildToolsVersion}/aapt2";
        };
      });
}
