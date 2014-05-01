/*
 * Filter script configuration. This is environment-aware if you choose to make it so.
 * 
 * This config is not merged with the default config, but overrides it.
 * 
 * Structure is 
 * types {
 * 		Type {
 * 			filters {
 * 				all = [/\s+/] // array of patterns. The field name 'all' applies to all fields. If you need to reuse patterns across several but not all fields, declare them separately.
 * 				field_name = [pattern variable]
 * 			}
 * 		}
 * }
 */
pattern {
	whiteSpace = /\s+/
}
types {
    People {
        filters {
			all = [/\bBatman\b/] // fail all Batman instances
            Email = [
				pattern.whiteSpace, // fail all whitespaces
				/^(?!.*@example.edu.au).*$/ // fail all emails not ending with '@example.edu.au'
				]
        }
    }
}
