#!/usr/bin/env bash

cd "$(dirname "${BASH_SOURCE[0]}")/.." || exit 1

java -cp "target/diffdetective-$(./scripts/version.sh)-jar-with-dependencies.jar" org.variantsync.diffdetective.experiments.views.Main "docs/datasets/views.md" &&
echo "runValidation.sh DONE"
