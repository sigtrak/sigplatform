<?php

/**
 * @file
 * API documentation for hooks provided by the nodechanges module.
 */

/**
 * Implements hook_nodechanges_file_changes_element_alter()
 *
 * Allows external modules to modify the $items array of files before
 * rendering the extended_file_field formatter output.
 *
 * @param array $element
 *   A render array to display the files changed in a given node revision.
 *   This contains the following keys:
 *   - #rows: Array of table rows representing each file that changed.
 *   - #files: Array of fully-loaded file objects that have changed.
 *   - #field_name: String with the name of the file field being rendered.
 *   - #header: Optional array of table headers to use.
 * @param array $context
 *   Associative array of context for the changed files element being altered,
 *   with the following keys:
 *   - entity_type: String with the type of entity nodechanges is attached to.
 *   - bundle: String with the type of bundle nodechanges is attached to.
 *   - entity: An object representing the entity nodechanges is attached to.
 *   - langcode: The language associated with the element.
 *   - display: The display settings to use, as found in the 'display' entry of
 *     the instance definition.
 *
 * @see drupal_alter()
 * @see nodechanges_field_formatter_view()
 * @see theme_nodechanges_field_formatter_file_view()
 * @see theme_table()
 */
function hook_nodechanges_file_changes_element_alter(&$element, $context) {
  $new_rows = array();
  // Go through all the rows in the table, adding a rowspan to the first
  // column of each one, and then adding a second table row to go with it.
  foreach ($element['#rows'] as $fid => $row) {
    $new_rows[$fid] = array(
      array('data' => $row[0], 'rowspan' => 2),
      $row[1],
      $row[2],
    );
    $new_rows["$fid-results"] = array(
      array(
        'data' => t('Test results for %file go here', array('%file' => $element['#files'][$fid]->filename)),
        'colspan' => 2,
      ),
    );
  }
  $element['#rows'] = $new_rows;

  // Inject a header into the table.
  $element['#header'] = array(t('Status'), t('File'), t('Size'));
}

/**
 * Implements hook_nodechanges_get_changes()
 */
function hook_nodechanges_get_changes($node, $unchanged, &$items) {
  /**
   * Set "$items['nodechanges_skip'] = TRUE;" for preventing nodechanges to save comment
   */
}