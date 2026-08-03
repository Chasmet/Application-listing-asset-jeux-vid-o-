#!/usr/bin/env sh
set -eu

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle 8.7 n'est pas installé sur cette machine." >&2
echo "Installe Gradle 8.7 ou utilise le workflow GitHub Actions fourni." >&2
exit 1
