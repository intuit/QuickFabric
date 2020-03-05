DELIMITER $$
DROP PROCEDURE IF EXISTS patch_v1 $$

-- Create the stored procedure to perform patch
CREATE PROCEDURE patch_v1()

BEGIN
  SET @patch_id = null;
  SET @patch = NULL;
  SET @status= NULL;
  SET @patch_name ='v1';
  
SELECT 
    id, patch_name, patch_status
INTO @patch_id , @patch , @status FROM
    db_patch
WHERE
    patch_name = @patch_name
LIMIT 1;

SELECT @patch, @status;

IF(@patch IS NULL) THEN
	select 'inserting patch';
	insert into db_patch (patch_name, patch_status) values(@patch_name,'pending');
    
	SELECT 
		id, patch_name, patch_status
	INTO @patch_id , @patch , @status FROM
		db_patch
	WHERE
		patch_name = @patch_name
	LIMIT 1;
END IF;

IF(@patch IS NOT NULL AND @status = "pending") THEN

-- do patch work
select 'inserting into workflow';
INSERT INTO workflow (workflow_name, workflow_step, lookup_table)VALUES ('patch-workflow','mark_current_clusterrmination', now());

-- update patch status
SELECT 'updating patch status to completed';
UPDATE db_patch 
SET 
    patch_status = 'completed'
WHERE
    id = @patch_id;
END IF;

END $$

-- Execute the stored procedure
CALL patch_v1() $$

-- Don't forget to drop the stored procedure when you're done!
DROP PROCEDURE IF EXISTS patch_v1 $$

DELIMITER ;
