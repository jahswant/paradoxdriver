/*
 * ParadoxDataFile.java
 *
 * 03/12/2009
 * Copyright (C) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.paradox.parser.nodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a select node.
 * 
 * @author Leonardo Alves da Costa
 * @since 1.0
 * @version 1.1
 */
public class SelectNode extends StatementNode {

    /**
     * The conditions list.
     */
    private List<SQLNode> conditions;

    /**
     * If has a distinct token.
     */
    private boolean distinct;

    /**
     * The field list (SELECT).
     */
    private final ArrayList<SQLNode> fields = new ArrayList<>();

    /**
     * Group by values.
     */
    private final ArrayList<IdentifierNode> groups = new ArrayList<>();

    /**
     * Order by values.
     */
    private final ArrayList<IdentifierNode> order = new ArrayList<>();

    /**
     * The tables in from token.
     */
    private final ArrayList<TableNode> tables = new ArrayList<>();

    /**
     * Create a new instance.
     */
    public SelectNode() {
        super("SELECT");
    }

    /**
     * Adds the field in the list.
     * 
     * @param field
     *            the field to add.
     */
    public void addField(final SQLNode field) {
        fields.add(field);
    }

    /**
     * Adds the group by identifier.
     * 
     * @param indentifier
     *            the group by identifier to add.
     */
    public void addGroupBy(final IdentifierNode indentifier) {
        groups.add(indentifier);
    }

    /**
     * Adds the order by identifier.
     * 
     * @param indentifier
     *            the order by identifier to add.
     */
    public void addOrderBy(final IdentifierNode indentifier) {
        order.add(indentifier);
    }

    /**
     * Adds the table in list.
     * 
     * @param table
     *            the table to add
     */
    public void addTable(final TableNode table) {
        tables.add(table);
    }

    /**
     * Gets the condition list.
     * 
     * @return the condition list.
     */
    public List<SQLNode> getConditions() {
        return conditions;
    }

    /**
     * Gets the field list.
     * 
     * @return the field list.
     */
    public List<SQLNode> getFields() {
        return fields;
    }

    /**
     * Gets the group list.
     * 
     * @return the group list.
     */
    public List<IdentifierNode> getGroups() {
        return groups;
    }

    /**
     * Gets the order by list.
     * 
     * @return the order by list.
     */
    public List<IdentifierNode> getOrder() {
        return order;
    }

    /**
     * Gets the table list.
     * 
     * @return the table list.
     */
    public List<TableNode> getTables() {
        return tables;
    }

    /**
     * Get if this select has a distinct token.
     * 
     * @return true if this select has a distinct token.
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * Sets the condition list.
     * 
     * @param conditions
     *            the condition list.
     */
    public void setConditions(final List<SQLNode> conditions) {
        this.conditions = conditions;
    }

    /**
     * Sets the distinct key present.
     * 
     * @param distinct
     *            the distinct key present.
     */
    public void setDistinct(final boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName());
        builder.append(" ");

        boolean first = true;
        for (final SQLNode field : fields) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(field);
        }
        if (!tables.isEmpty()) {
            builder.append(" FROM ");
            first = true;
            for (final TableNode table : tables) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(table);
            }
        }
        if (conditions != null && conditions.isEmpty()) {
            builder.append(" WHERE ");
            first = true;
            for (final SQLNode cond : conditions) {
                if (first) {
                    first = false;
                } else {
                    builder.append(" ");
                }
                builder.append(cond);
            }
        }
        if (groups.isEmpty()) {
            builder.append(" GROUP BY ");
            first = true;
            for (final IdentifierNode group : groups) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(group);
            }
        }
        if (order.isEmpty()) {
            builder.append(" ORDER BY ");
            first = true;
            for (final IdentifierNode ident : order) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(ident);
            }
        }
        return builder.toString();
    }
}
