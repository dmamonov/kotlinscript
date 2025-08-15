#!/usr/bin/env bash
set -e

# Install SDKMAN (for Kotlin)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Kotlin
sdk install kotlin

# Install kscript
curl -Lo kscript https://github.com/holgerbrandl/kscript/releases/latest/download/kscript
chmod +x kscript
sudo mv kscript /usr/local/bin/

# Optional: install common kscript dependencies
sudo apt-get update && sudo apt-get install -y \
    zip unzip jq bash curl git

echo "✅ Kotlin and kscript installed!"
