#!/usr/bin/env bash
set -euo pipefail

# ---------------- SDKMAN location ----------------
SDKMAN_DIR="${SDKMAN_DIR:-/usr/local/sdkman}"
INIT="$SDKMAN_DIR/bin/sdkman-init.sh"
if [[ ! -f "$INIT" ]]; then
  echo "SDKMAN not found at $INIT. Installing user-local under /home/vscode/.sdkman ..."
  curl -s "https://get.sdkman.io" | bash
  SDKMAN_DIR="/home/vscode/.sdkman"
  INIT="$SDKMAN_DIR/bin/sdkman-init.sh"
fi

# ---------------- Env for SDKMAN (avoid nounset issues) ----------------
: "${SDKMAN_CANDIDATES_API:=https://api.sdkman.io/2}"
: "${SDKMAN_CHECKSUM_SERVICE:=https://api.sdkman.io/2/checksum}"
: "${SDKMAN_DEBUG_MODE:=false}"
: "${SDKMAN_INSECURE_SSL:=false}"
: "${SDKMAN_OFFLINE_MODE:=false}"
: "${SDKMAN_ROAMING_MODE:=false}"
: "${SDKMAN_AUTO_ANSWER:=true}"   # non-interactive installs
export SDKMAN_DIR SDKMAN_CANDIDATES_API SDKMAN_CHECKSUM_SERVICE \
       SDKMAN_DEBUG_MODE SDKMAN_INSECURE_SSL SDKMAN_OFFLINE_MODE \
       SDKMAN_ROAMING_MODE SDKMAN_AUTO_ANSWER

# Some shells read these in the init script; define to silence -u
: "${ZSH_VERSION:=}"
: "${BASH_VERSION:=${BASH_VERSION-}}"

# Source SDKMAN with -u off (its scripts expect unset vars to be okay)
set +u
# shellcheck disable=SC1090
source "$INIT"
set -u

# ---------------- Essentials ----------------
export DEBIAN_FRONTEND=noninteractive
sudo apt-get update -y
sudo apt-get install -y curl zip unzip jq ca-certificates

# ---------------- Kotlin via SDKMAN ----------------
set +u
yes | sdk install kotlin || yes | sdk upgrade kotlin || true
set -u

# # ---------------- kscript via SDKMAN, with guarded fallback ----------------
# set +u
# yes | sdk install kscript || true
# set -u

# if ! command -v kscript >/dev/null 2>&1; then
#   echo "SDKMAN did not provide kscript; using GitHub release…"
#   # Current canonical org is kscript-lang
#   url="https://github.com/kscript-lang/kscript/releases/latest/download/kscript"
#   # -f: fail on HTTP errors, -L: follow redirects, -o: output file
#   if curl -fL -o /usr/local/bin/kscript "$url"; then
#     chmod +x /usr/local/bin/kscript
#   else
#     echo "Fallback download failed: $url"
#     exit 1
#   fi
# fi

# ---------------- Sanity checks ----------------
java -version
(kotlinc -version || kotlin -version) || true
kscript --version || true

echo "✅ Kotlin + kscript ready."
