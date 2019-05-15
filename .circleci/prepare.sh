#!/bin/bash

: ${GH_REPO:=iBug/USTC-RV-Chisel} ${BRANCH:=gh-pages}

set -e

e_info() {
  echo -e "\x1B[36;1m[Info]\x1B[0m $*" >&2
}

e_success() {
  echo -e "\x1B[32;1m[Success]\x1B[0m $*" >&2
}

e_warning() {
  echo -e "\x1B[33;1m[Warning]\x1B[0m $*" >&2
}

e_error() {
  echo -e "\x1B[31;1m[Error]\x1B[0m $*" >&2
}

e_info "Cloning from GitHub:$GH_REPO.git, branch=$BRANCH"

if [ -n "$SSH_KEY_E" ]; then  # Prefer SSH key
  base64 -d <<< "$SSH_KEY_E" | gunzip -c > ~/.ssh/id_rsa
  SSH_AUTH_SOCK=none \
  GIT_SSH_COMMAND="ssh -i ~/.ssh/id_rsa" \
  git clone --depth=3 --branch=$BRANCH --single-branch "git@github.com:$GH_REPO.git" _site
else
  e_error "SSH key not found, not continuing."
fi

e_success "Done"
