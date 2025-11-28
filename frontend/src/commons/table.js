import React, { Component } from "react";
import { Table as BSTable, Row, Col, Form, Pagination, Button } from "react-bootstrap";

class Table extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data: props.data || [],
      columns: props.columns || [],
      search: props.search || [],
      filters: [],
      pageSize: props.pageSize || 10,
      currentPage: 1,
    };
  }

  componentDidUpdate(prevProps) {
    if (prevProps.data !== this.props.data) {
      this.setState({ data: this.props.data });
    }
  }

  handleChange(value, index, accessor) {
    const filters = [...this.state.filters];
    filters[index] = { accessor, value: value.target.value };
    this.setState({ filters, currentPage: 1 });
  }

  applyFilters(data) {
    if (!this.state.filters.length) return data;

    return data.filter(row =>
      this.state.filters.every(f => {
        if (!f) return true;
        const cell = row[f.accessor];
        return String(cell).toLowerCase().includes(String(f.value).toLowerCase());
      })
    );
  }

  handlePageChange(page) {
    this.setState({ currentPage: page });
  }

  renderPagination(totalPages) {
    if (totalPages <= 1) return null;

    let items = [];
    for (let number = 1; number <= totalPages; number++) {
      items.push(
        <Pagination.Item
          key={number}
          active={number === this.state.currentPage}
          onClick={() => this.handlePageChange(number)}
        >
          {number}
        </Pagination.Item>
      );
    }

    return <Pagination>{items}</Pagination>;
  }

  render() {
    const { data, columns, search, pageSize, currentPage } = this.state;
    const { onView, onEdit, onDelete, extraActions } = this.props;

    // Filter data
    const filteredData = this.applyFilters(data);

    // Pagination
    const totalPages = Math.ceil(filteredData.length / pageSize);
    const startIndex = (currentPage - 1) * pageSize;
    const paginatedData = filteredData.slice(startIndex, startIndex + pageSize);

    return (
      <div>
        {/* Search filters */}
        {search.length > 0 && (
          <Row className="mb-3">
            {search.map((s, idx) => (
              <Col key={idx}>
                <Form.Group>
                  <Form.Label>{s.accessor}</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder={`Search ${s.accessor}`}
                    onChange={(e) => this.handleChange(e, idx, s.accessor)}
                  />
                </Form.Group>
              </Col>
            ))}
          </Row>
        )}

        {/* Table */}
        <BSTable striped bordered hover responsive>
          <thead>
            <tr>
              {columns.map((col, idx) => (
                <th key={idx}>{col.Header}</th>
              ))}
              {(onView || onEdit || onDelete || extraActions) && <th>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {paginatedData.map((row, rowIdx) => (
              <tr key={rowIdx}>
                {columns.map((col, colIdx) => (
                  <td key={colIdx}>{row[col.accessor]}</td>
                ))}
                {(onView || onEdit || onDelete || extraActions) && (
                  <td>
                    {onView && <Button size="sm" variant="info" onClick={() => onView(row)}>View</Button>}{' '}
                    {onEdit && <Button size="sm" variant="warning" onClick={() => onEdit(row)}>Edit</Button>}{' '}
                    {onDelete && <Button size="sm" variant="danger" onClick={() => onDelete(row)}>Delete</Button>}
                    {extraActions && extraActions.map((action, idx) => (
    <Button
      key={idx}
      size="sm"
      variant={action.variant || "secondary"}
      onClick={() => action.onClick(row)}
      className="ms-1"
    >
      {action.label}
    </Button>
  ))}
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </BSTable>

        {/* Pagination */}
        <div className="d-flex justify-content-center mt-3">
          {this.renderPagination(totalPages)}
        </div>
      </div>
    );
  }
}

export default Table;
