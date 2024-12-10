create table exam (
    student string,
    subject string,
    grade int
);

INSERT INTO exam VALUES
('A', '英语', 70),
('A', '数学', 56),
('B', '语文', 97),
('C', '英语', 88);



WITH to_list AS (
    SELECT student
          ,if(subject = '数学', grade, NULL) AS sx
          ,if(subject = '英语', grade, NULL) AS yy
          ,if(subject = '语文', grade, NULL) AS yw
    FROM exam
),agg_by_student AS (
    SELECT student
          ,collect_set(sx) AS sx
          ,collect_set(yy) AS yy
          ,collect_set(yw) AS yw
    FROM to_list
    GROUP BY student
)
SELECT student
    ,sx[0] AS sx
    ,yy[0] AS yy
    ,yw[0] AS yw
FROM agg_by_student