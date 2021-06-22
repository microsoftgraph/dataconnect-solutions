export function getRandomColor(index: number) {
  const colors = [
    '#7EE8B4',
    '#FFD294',
    '#FFB1A7',
    '#DEABFB',
    '#A9CBFD',
    'rgb(255, 178, 174)',
    'rgb(253, 153, 213)',
    'rgb(214, 171, 251)',
    'rgb(169, 203, 253)',
    'rgb(125, 236, 253)'
  ];
  return colors[index % colors.length];
}

export const chartColors = [
  '#A686F7',
  '#00CC81',
  '#9acc00',
  '#FF5A4A',
  '#FFB700',
  '#478EFF'
];
