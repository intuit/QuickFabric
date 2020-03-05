const VisOptions = {
    autoResize: true,
    height: '100%',
    width: '100%',
    nodes: {
        shape: 'box',
        color: '#FFFFFF',
        font: {
          multi: 'html',
          bold: '30px Avenir #1d51b8',
          face: 'Avenir',
        },
      },
      edges: {
        smooth: {
          type: 'cubicBezier',
          roundness: 0.4,
          forceDirection: 'horizontal',
        },
        arrows: 'to',
      },
      layout: {
        improvedLayout: true,
        hierarchical: {
          direction: 'LR',
          levelSeparation: 350,
          nodeSpacing: 50,
          sortMethod: 'directed',
          blockShifting: true,
          edgeMinimization: true,
        },
      },
      interaction: {
        hover: true,
        tooltipDelay: 100,
        navigationButtons: true,
        zoomView: true,
      },
      physics: false,
      groups: {
        ACCOUNT: {
          shape: 'box',
          borderWidth: 4,
          font: {
            size: 30
          }
        },
        ACT: {
          shape: 'ellipse',
          borderWidth: 4,
          font: {
            size: 25,
          },
        },
        DATA: {
          shape: 'box',
          shapeProperties: {
            borderRadius: 24,
          },
          font: {
            size: 20,
          },
        },
        COST: {
          shape: 'ellipse',
          shapeProperties: {
            borderRadius: 24,
          },
          font: {
            size: 20,
          },
        },
        ACT_TYPE: {
          shape: 'box',
          borderWidth: 4,
          shapeProperties: {
            borderRadius: 0,
          },
          font: {
            size: 20,
          },
        },
      }
  };

  export default VisOptions;