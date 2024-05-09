package repository

import "errors"

var (
	ErrNotExists     = errors.New("data does not exist")
	ErrAlreadyExists = errors.New("data already exists")

	ErrFieldViolation = errors.New("field violation")

	DbErrCodeAlreadyExists = []string{
		"23505", // unique_violation
	}
	DbErrCodeFieldViolation = []string{
		"23000", // integrity_constraint_violation
		"23501", // restrict_violation
		"23502", // not_null_violation
		"23503", // foreign_key_violation
		"23514", // check_violation
		"23P01", // exclusion_violation
	}
	DbErrCodeNotFound = []string{
		"20000", // case_not_found
	}
)
