package io.github.udaychandra.susel.tool.plugin.impl;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.JavaExec;
import io.github.udaychandra.susel.tool.plugin.SuselPlugin;

/**
 * Runs the Susel tool to generate metadata on the module's service providers.
 */
public class SuselMetadataTask extends JavaExec {
    private static final Logger LOGGER = Logging.getLogger(SuselPlugin.class);

    // TODO: Support incremental builds by defining i/p and o/p properties.
}
